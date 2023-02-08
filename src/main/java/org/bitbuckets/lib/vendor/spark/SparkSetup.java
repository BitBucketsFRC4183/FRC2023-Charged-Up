package org.bitbuckets.lib.vendor.spark;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel;
import com.revrobotics.REVPhysicsSim;
import com.revrobotics.SparkMaxLimitSwitch;
import edu.wpi.first.math.system.plant.DCMotor;
import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.ProcessPath;
import org.bitbuckets.lib.StartupProfiler;
import org.bitbuckets.lib.control.PIDConfig;
import org.bitbuckets.lib.hardware.IMotorController;
import org.bitbuckets.lib.hardware.MotorConfig;
import org.bitbuckets.lib.log.ILoggable;
import org.bitbuckets.lib.log.LoggingConstants;
import org.bitbuckets.lib.tune.IValueTuner;

import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;
import static org.bitbuckets.lib.vendor.spark.RevUtils.checkNeoError;

/**
 * SparkPIDSetup is for control based shit
 * SparkPercentSetup is for your normal drive or whatever
 */
public class SparkSetup implements ISetup<IMotorController> {

    static final List<Integer> seen = new ArrayList<>();

    final int canId;
    final MotorConfig motorConfig;
    final PIDConfig pidConfig;

    public SparkSetup(int canId, MotorConfig motorConfig, PIDConfig pidConfig) {
        this.canId = canId;
        this.motorConfig = motorConfig;
        this.pidConfig = pidConfig;
    }

    @Override
    public IMotorController build(ProcessPath self) {

        StartupProfiler configError = self.generateSetupProfiler("config-error");
        configError.markProcessing();

        //check id for duplicate usage
        if (seen.contains(canId)) {
            configError.markErrored(
                    new IllegalStateException(
                            format("duplicate sparkmax usage of id %s", canId)
                    )
            );
        }
        seen.add(canId);

        //vendor
        CANSparkMax spark = new CANSparkMax(canId, CANSparkMaxLowLevel.MotorType.kBrushless);
        spark.restoreFactoryDefaults(); //defaulitesi
        spark.enableVoltageCompensation(12.0);

        if (motorConfig.shouldBreakOnNoCommand) {
            spark.setIdleMode(CANSparkMax.IdleMode.kBrake);
        } else {
            spark.setIdleMode(CANSparkMax.IdleMode.kCoast);
        }

        spark.setInverted(motorConfig.isInverted);
        spark.setSmartCurrentLimit((int) motorConfig.currentLimit);

        SparkMaxLimitSwitch forwardSwitch = null;
        SparkMaxLimitSwitch reverseSwitch = null;

        if (motorConfig.isForwardLimitEnabled) {
            forwardSwitch = spark.getForwardLimitSwitch(SparkMaxLimitSwitch.Type.kNormallyOpen);
            forwardSwitch.enableLimitSwitch(true);
        }

        if (motorConfig.isBackwardLimitEnabled) {
            reverseSwitch = spark.getReverseLimitSwitch(SparkMaxLimitSwitch.Type.kNormallyOpen);
            reverseSwitch.enableLimitSwitch(true);
        }


        ILoggable<double[]> data = self.generateDoubleLoggers("appliedOutput", "busVoltage", "positionRotations", "velocityRotatations", "setpointRotations", "error");

        // setup tuneable pid
        if (pidConfig.kP == 0 && pidConfig.kI == 0 && pidConfig.kD == 0) {
            IValueTuner<Double> p = self.generateValueTuner("p", pidConfig.kP);
            IValueTuner<Double> i = self.generateValueTuner("i", pidConfig.kI);
            IValueTuner<Double> d = self.generateValueTuner("d", pidConfig.kD);
            var pidController = spark.getPIDController();
            SparkTuningAspect sparkTuningAspect = new SparkTuningAspect(p, i, d, pidController);
            pidController.setP(p.consumeValue());
            pidController.setI(i.consumeValue());
            pidController.setD(d.consumeValue());
            self.registerLoop(sparkTuningAspect, LoggingConstants.TUNING_PERIOD, "tuning-loop");
        } else {
            checkNeoError(spark.getPIDController().setP(pidConfig.kP), "Failed to set NEO PID proportional constant");
            checkNeoError(spark.getPIDController().setI(pidConfig.kI), "Failed to set NEO PID integral constant");
            checkNeoError(spark.getPIDController().setD(pidConfig.kD), "Failed to set NEO PID derivative constant");
        }

        SparkRelativeMotorController ctrl = new SparkRelativeMotorController(motorConfig, spark, data);

        self.registerLoop(ctrl, LoggingConstants.LOGGING_PERIOD, "logging-loop");
        if (forwardSwitch != null) {
            ILoggable<Boolean> loggable = self.generateBooleanLogger("forwardSwitchPressed");
            SparkLimitLoggingAspect loggingAspect = new SparkLimitLoggingAspect(loggable, forwardSwitch);
            self.registerLoop(loggingAspect, LoggingConstants.LOGGING_PERIOD, "forw-log-loop");
        }
        if (reverseSwitch != null) {
            ILoggable<Boolean> loggable = self.generateBooleanLogger("reverseSwitchPressed");
            SparkLimitLoggingAspect loggingAspect = new SparkLimitLoggingAspect(loggable, reverseSwitch);
            self.registerLoop(loggingAspect, LoggingConstants.LOGGING_PERIOD, "revr-log-loop");
        }

        REVPhysicsSim.getInstance().addSparkMax(spark, DCMotor.getNeo550(1));

        configError.markCompleted();
        return ctrl;
    }

}