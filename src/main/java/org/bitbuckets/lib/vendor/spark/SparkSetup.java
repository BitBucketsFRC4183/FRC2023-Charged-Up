package org.bitbuckets.lib.vendor.spark;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel;
import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.ProcessPath;
import org.bitbuckets.lib.hardware.IMotorController;
import org.bitbuckets.lib.hardware.MotorControllerDataAutoGen;
import org.bitbuckets.lib.hardware.MotorIndex;
import org.bitbuckets.lib.log.DataLogger;
import org.bitbuckets.lib.SetupProfiler;
import org.bitbuckets.lib.tune.IValueTuner;
import org.bitbuckets.lib.vendor.LoggingConstants;
import org.bitbuckets.robot.RobotConstants;

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
    final double[] motorConstants;

    SparkSetup(int canId, double[] motorConstants) {
        this.canId = canId;
        this.motorConstants = motorConstants;
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
        spark.enableVoltageCompensation(12.0);

        if (motorConstants[MotorIndex.IS_BRAKE] == 1.0) {
            spark.setIdleMode(CANSparkMax.IdleMode.kBrake);
        } else {
            spark.setIdleMode(CANSparkMax.IdleMode.kCoast);
        }

        boolean inverted = motorConstants[MotorIndex.INVERTED] == 1.0;
        spark.setInverted(inverted);
        spark.setSmartCurrentLimit((int) motorConstants[MotorIndex.CURRENT_LIMIT]);

        DataLogger<MotorControllerDataAutoGen> logger = path.generatePushDataLogger(MotorControllerDataAutoGen::new);

        IValueTuner<Double> p = path.generateValueTuner("p", 0.0);
        IValueTuner<Double> i = path.generateValueTuner("i", 0.0);
        IValueTuner<Double> d = path.generateValueTuner("d", 0.0);

        SparkTuningAspect sparkTuningAspect = new SparkTuningAspect(p, i, d, spark.getPIDController());
        SparkRelativeMotorController ctrl = new SparkRelativeMotorController(motorConstants, spark, logger);

        path.registerLoop(ctrl, LoggingConstants.LOGGING_PERIOD, "logging-loop");
        path.registerLoop(sparkTuningAspect, LoggingConstants.TUNING_PERIOD, "tuning-loop");

        return ctrl;
    }

}
