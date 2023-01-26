package org.bitbuckets.lib.vendor.spark;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel;
import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.ProcessPath;
import org.bitbuckets.lib.hardware.IMotorController;
import org.bitbuckets.lib.hardware.MotorIndex;
import org.bitbuckets.lib.log.DataLogger;
import org.bitbuckets.lib.tune.IValueTuner;
import org.bitbuckets.lib.vendor.ctre.TalonDataAutoGen;
import org.bitbuckets.robot.RobotConstants;

/**
 * SparkPIDSetup is for control based shit
 * SparkPercentSetup is for your normal drive or whatever
 */
public class SparkSetup implements ISetup<IMotorController> {

    final int canId;
    final double[] motorConstants;

    SparkSetup(int canId, double[] motorConstants) {
        this.canId = canId;
        this.motorConstants = motorConstants;
    }

    @Override
    public IMotorController build(ProcessPath path) {
        IValueTuner<double[]> pid = path.generateTuneable("pid", RobotConstants.DEFAULT_PID_CONSTANTS);

        CANSparkMax spark = new CANSparkMax(canId, CANSparkMaxLowLevel.MotorType.kBrushless);

        spark.enableVoltageCompensation(12.0);
        spark.setIdleMode(CANSparkMax.IdleMode.kBrake);
        boolean inverted = motorConstants[MotorIndex.INVERTED] == 1.0;
        spark.setInverted(inverted);

        DataLogger<TalonDataAutoGen> logger = path.generatePushDataLogger(TalonDataAutoGen::new);
        SparkTuningAspect sparkTuningAspect = new SparkTuningAspect(pid, spark.getPIDController());

        path.registerLoop(sparkTuningAspect, 200, "pid");
        return new SparkRelativeMotorController(motorConstants, spark, logger);
    }

}
