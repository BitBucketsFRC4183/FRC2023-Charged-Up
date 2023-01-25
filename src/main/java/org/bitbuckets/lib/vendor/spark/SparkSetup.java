package org.bitbuckets.lib.vendor.spark;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel;
import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.ProcessPath;
import org.bitbuckets.lib.hardware.IMotorController;
import org.bitbuckets.lib.hardware.MotorIndex;
import org.bitbuckets.lib.hardware.PIDIndex;
import org.bitbuckets.lib.log.StartupLogger;
import org.bitbuckets.lib.tune.IValueTuner;

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
        StartupLogger libSetup = path.generateStartupLogger("lib-setup");

        IValueTuner<double[]> pid = path.generateTuneable("pid", PIDIndex.CONSTANTS(0,0,0,0,0));

        CANSparkMax spark = new CANSparkMax(canId, CANSparkMaxLowLevel.MotorType.kBrushless);
        spark.enableVoltageCompensation(12.0);
        spark.setIdleMode(CANSparkMax.IdleMode.kBrake);
        boolean inverted = motorConstants[MotorIndex.INVERTED] == 1.0;
        spark.setInverted(inverted);




        return null;
    }

    //abstract protected IMotorController childSpecificTasks(ProcessPath path, CANSparkMax sparkMax);
}
