package org.bitbuckets.lib.vendor.spark;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel;
import com.revrobotics.SparkMaxLimitSwitch;
import org.bitbuckets.lib.IProcess;
import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.control.PIDConfig;
import org.bitbuckets.lib.hardware.IMotorController;
import org.bitbuckets.lib.hardware.MotorConfig;
import org.bitbuckets.lib.log.IConsole;
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
    public IMotorController build(IProcess self) {

        IConsole console = self.getAssociatedConsole();

        //check id for duplicate usage
        if (seen.contains(canId)) {
            console.sendError(
                    new IllegalStateException(format("duplicate sparkmax usage of id %s", canId))
            );
        }
        seen.add(canId);

        //vendor
        CANSparkMax spark = new CANSparkMax(canId, CANSparkMaxLowLevel.MotorType.kBrushless);
        spark.restoreFactoryDefaults(); //defaulitesi
        spark.enableVoltageCompensation(12.0);

        if (motorConfig.shouldBreakOnNoCommand) {
            console.sendInfo("using brake mode");
            spark.setIdleMode(CANSparkMax.IdleMode.kBrake);
        } else {
            console.sendInfo("using coast mode");
            spark.setIdleMode(CANSparkMax.IdleMode.kCoast);
        }

        spark.setInverted(motorConfig.isInverted);
        spark.setSmartCurrentLimit((int) motorConfig.currentLimit);

        // Uncomment this when doing something that may command motors to full throttle by accident
        //spark.getPIDController().setOutputRange(-0.3,0.3);

        SparkMaxLimitSwitch forwardSwitch = null;
        SparkMaxLimitSwitch reverseSwitch = null;

        if (motorConfig.isForwardHardLimitEnabled) {
            console.sendInfo("using forward limit switch!");
            forwardSwitch = spark.getForwardLimitSwitch(SparkMaxLimitSwitch.Type.kNormallyOpen);
            forwardSwitch.enableLimitSwitch(true);
        }

        if (motorConfig.isBackwardHardLimitEnabled) {
            console.sendInfo("using backward limit switch!");
            reverseSwitch = spark.getReverseLimitSwitch(SparkMaxLimitSwitch.Type.kNormallyOpen);
            reverseSwitch.enableLimitSwitch(true);
        }


        // setup tuneable pid
        if (pidConfig.kP == 0 && pidConfig.kI == 0 && pidConfig.kD == 0) {
            console.sendInfo("using tuneable pid!");

            IValueTuner<Double> p = self.generateTuner(Double.class, "p", pidConfig.kP);
            IValueTuner<Double> i = self.generateTuner(Double.class, "i", pidConfig.kI);
            IValueTuner<Double> d = self.generateTuner(Double.class, "d", pidConfig.kD);
            var pidController = spark.getPIDController();
            SparkTuner sparkTuner = new SparkTuner(p, i, d, pidController);
            pidController.setP(p.consumeValue());
            pidController.setI(i.consumeValue());
            pidController.setD(d.consumeValue());
            self.registerLogicLoop(sparkTuner);
        } else {
            console.sendInfo("using hardcoded pid!");

            checkNeoError(spark.getPIDController().setP(pidConfig.kP), "Failed to set NEO PID proportional constant");
            checkNeoError(spark.getPIDController().setI(pidConfig.kI), "Failed to set NEO PID integral constant");
            checkNeoError(spark.getPIDController().setD(pidConfig.kD), "Failed to set NEO PID derivative constant");
        }

        SparkRelativeMotorController ctrl = new SparkRelativeMotorController(motorConfig, spark);
        OnboardPidLogger onboardPidLogger = new OnboardPidLogger(
                ctrl,
                self.generateLogger(Double.class, "pos-setpoint-mechanism-rotations"),
                self.generateLogger(Double.class, "encoder-mechanism-rotations"),
                self.generateLogger(Double.class, "error-mechanism-rotations"),
                self.generateLogger(LastControlMode.class, "last-control-mode")
        );

        self.registerLogLoop(onboardPidLogger);

        if (forwardSwitch != null) {
            LimitSwitchLogger loggingAspect = new LimitSwitchLogger(
                    self.generateLogger(Boolean.class,"forward-hard-switch-pressed"),
                    forwardSwitch
            );
            self.registerLogLoop(loggingAspect);
        }
        if (reverseSwitch != null) {
            LimitSwitchLogger loggingAspect = new LimitSwitchLogger(
                    self.generateLogger(Boolean.class,"reverse-hard-switch-pressed"),
                    reverseSwitch
            );
            self.registerLogLoop(loggingAspect);
        }

        return ctrl;
    }

}