package org.bitbuckets.lib.vendor.spark;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel;
import com.revrobotics.REVPhysicsSim;
import com.revrobotics.SparkMaxLimitSwitch;
import edu.wpi.first.math.system.plant.DCMotor;
import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.ProcessPath;
import org.bitbuckets.lib.SetupProfiler;
import org.bitbuckets.lib.hardware.IMotorController;
import org.bitbuckets.lib.hardware.MotorConfig;
import org.bitbuckets.lib.log.ILoggable;
import org.bitbuckets.lib.log.LoggingConstants;
import org.bitbuckets.lib.tune.IValueTuner;

import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;

/**
 * SparkPIDSetup is for control based shit
 * SparkPercentSetup is for your normal drive or whatever
 */
public class SparkSetup implements ISetup<IMotorController> {

    static final List<Integer> seen = new ArrayList<>();


    final int canId;
    final MotorConfig motorConfig;

    public SparkSetup(int canId, MotorConfig motorConfig) {
        this.canId = canId;
        this.motorConfig = motorConfig;
    }

    @Override
    public IMotorController build(ProcessPath path) {

        SetupProfiler configError = path.generateSetupProfiler("config-error");
        configError.markProcessing();

        //check id for duplicate usage
        if (seen.contains(canId)) {
            configError.markErrored(format("duplicate sparkmax usage of id %s", canId));
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

        ILoggable<double[]> data = path.generateDoubleLoggers("appliedOutput", "busVoltage", "positionRotations", "velocityRotatations");
        IValueTuner<Double> p = path.generateValueTuner("p", 0.0);
        IValueTuner<Double> i = path.generateValueTuner("i", 0.0);
        IValueTuner<Double> d = path.generateValueTuner("d", 0.0);

        SparkTuningAspect sparkTuningAspect = new SparkTuningAspect(p, i, d, spark.getPIDController());
        SparkRelativeMotorController ctrl = new SparkRelativeMotorController(motorConfig, spark, data);

        path.registerLoop(ctrl, LoggingConstants.LOGGING_PERIOD, "logging-loop");
        path.registerLoop(sparkTuningAspect, LoggingConstants.TUNING_PERIOD, "tuning-loop");

        if (forwardSwitch != null) {
            ILoggable<Boolean> loggable = path.generateBooleanLogger("forwardSwitchPressed");
            SparkLimitLoggingAspect loggingAspect = new SparkLimitLoggingAspect(loggable, forwardSwitch);
            path.registerLoop(loggingAspect, LoggingConstants.LOGGING_PERIOD, "forw-log-loop");
        }
        if (reverseSwitch != null) {
            ILoggable<Boolean> loggable = path.generateBooleanLogger("reverseSwitchPressed");
            SparkLimitLoggingAspect loggingAspect = new SparkLimitLoggingAspect(loggable, reverseSwitch);
            path.registerLoop(loggingAspect, LoggingConstants.LOGGING_PERIOD, "revr-log-loop");
        }

        REVPhysicsSim.getInstance().addSparkMax(spark, DCMotor.getNeo550(1));

        configError.markCompleted();
        return ctrl;
    }

}
