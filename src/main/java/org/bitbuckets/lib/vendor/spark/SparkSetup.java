package org.bitbuckets.lib.vendor.spark;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel;
import com.revrobotics.SparkMaxLimitSwitch;
import org.bitbuckets.lib.ILogAs;
import org.bitbuckets.lib.IProcess;
import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.ITuneAs;
import org.bitbuckets.lib.control.PIDConfig;
import org.bitbuckets.lib.hardware.IMotorController;
import org.bitbuckets.lib.hardware.MotorConfig;
import org.bitbuckets.lib.hardware.OptimizationMode;
import org.bitbuckets.lib.tune.IValueTuner;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * SparkPIDSetup is for control based shit
 * SparkPercentSetup is for your normal drive or whatever
 */
public class SparkSetup implements ISetup<IMotorController> {

    static final List<Integer> seen = new ArrayList<>();

    final int canId;
    final MotorConfig motorConfig;
    final PIDConfig pidConfig;

    final Optional<SparkSetup> follower; //This will follow

    public SparkSetup(int canId, MotorConfig motorConfig, PIDConfig pidConfig, Optional<SparkSetup> follower) {
        this.canId = canId;
        this.motorConfig = motorConfig;
        this.pidConfig = pidConfig;
        this.follower = follower;
    }

    @Override
    public IMotorController build(IProcess self) {

        //check id for duplicate usage
        if (seen.contains(canId)) {
            throw new IllegalStateException();
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

        // Uncomment this when doing something that may command motors to full throttle by accident
        //spark.getPIDController().setOutputRange(-0.3,0.3);

        SparkMaxLimitSwitch forwardSwitch = null;
        SparkMaxLimitSwitch reverseSwitch = null;

        if (motorConfig.isForwardHardLimitEnabled) {
            //forwardSwitch = spark.getForwardLimitSwitch(SparkMaxLimitSwitch.Type.kNormallyOpen);
            //forwardSwitch.enableLimitSwitch(true);
        }

        if (motorConfig.isBackwardHardLimitEnabled) {
            //reverseSwitch = spark.getReverseLimitSwitch(SparkMaxLimitSwitch.Type.kNormallyOpen);
            //reverseSwitch.enableLimitSwitch(true);
        }


        IValueTuner<Double> p = self.generateTuner(ITuneAs.DOUBLE_INPUT, "p", pidConfig.kP);
        IValueTuner<Double> i = self.generateTuner(ITuneAs.DOUBLE_INPUT, "i", pidConfig.kI);
        IValueTuner<Double> d = self.generateTuner(ITuneAs.DOUBLE_INPUT,"d", pidConfig.kD);

        spark.getPIDController().setP(p.readValue());
        spark.getPIDController().setI(i.readValue());
        spark.getPIDController().setD(d.readValue());


        SparkTuner tuner = new SparkTuner(p,i,d, spark.getPIDController());
        self.registerLogicLoop(tuner);

        SparkRelativeMotorController ctrl = new SparkRelativeMotorController(motorConfig, spark);
        OnboardPidLogger onboardPidLogger = new OnboardPidLogger(
                ctrl,
                self.generateLogger(ILogAs.DOUBLE, "pos-setpoint-mechanism-rotations"),
                self.generateLogger(ILogAs.DOUBLE, "encoder-mechanism-rotations"),
                self.generateLogger(ILogAs.DOUBLE, "error-mechanism-rotations"),
                self.generateLogger(ILogAs.ENUM(LastControlMode.class),"last-control-mode")
        );

        self.registerLogLoop(onboardPidLogger);

        if (forwardSwitch != null) {
            LimitSwitchLogger loggingAspect = new LimitSwitchLogger(
                    self.generateLogger(ILogAs.BOOLEAN, "limit-forward"),
                    forwardSwitch
            );
            self.registerLogLoop(loggingAspect);
        }
        if (reverseSwitch != null) {
            LimitSwitchLogger loggingAspect = new LimitSwitchLogger(
                    self.generateLogger(ILogAs.BOOLEAN, "limit-reverse"),
                    reverseSwitch
            );
            self.registerLogLoop(loggingAspect);        }

        if (follower.isPresent()) {



            SparkRelativeMotorController follower = (SparkRelativeMotorController) self.childSetup(self.getSelfPath().getTail() + "-follower", this.follower.get());


            follower.rawAccess(CANSparkMax.class).follow(spark); //lmao dont do this typically
            //TODO optimize can frames for followers

        }

        //dont care about sticky faults eevery 100ms
        /*spark.setPeriodicFramePeriod(CANSparkMaxLowLevel.PeriodicFrame.kStatus0, 200);

        if (motorConfig.optimizationMode == OptimizationMode.OFFBOARD_VEL_PID || motorConfig.optimizationMode == OptimizationMode.LQR) {
            spark.setPeriodicFramePeriod(CANSparkMaxLowLevel.PeriodicFrame.kStatus1, 20);
        } else {
            spark.setPeriodicFramePeriod(CANSparkMaxLowLevel.PeriodicFrame.kStatus1, 200);
        }

        if (motorConfig.optimizationMode == OptimizationMode.OFFBOARD_POS_PID || motorConfig.optimizationMode == OptimizationMode.LQR) {
            spark.setPeriodicFramePeriod(CANSparkMaxLowLevel.PeriodicFrame.kStatus2, 20);
        } else {
            spark.setPeriodicFramePeriod(CANSparkMaxLowLevel.PeriodicFrame.kStatus2, 200);
        }*/

        return ctrl;
    }

}