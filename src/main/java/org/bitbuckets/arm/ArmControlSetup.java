package org.bitbuckets.arm;

import com.revrobotics.CANSparkMax;
import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.ProcessPath;
import org.bitbuckets.lib.hardware.IMotorController;
import org.bitbuckets.lib.log.Debuggable;
import org.bitbuckets.lib.vendor.spark.SparkSetup;

public class ArmControlSetup implements ISetup<ArmControl> {

    // Lower Device ID = 9
    // Upper Device ID = 3
    final ISetup<IMotorController> lowerJoint;
    final ISetup<IMotorController> upperJoint;


    public ArmControlSetup(ISetup<IMotorController> lowerJoint, SparkSetup sparkSetup) {
        this.lowerJoint = lowerJoint;
        this.upperJoint = sparkSetup;

    }


    @Override
    public ArmControl build(ProcessPath self) {

        var lower = lowerJoint.build(self.addChild("lower-joint"));
        var upper = upperJoint.build(self.addChild("upper-joint"));

        var lowerSpark = lower.rawAccess(CANSparkMax.class);
        var upperSpark = upper.rawAccess(CANSparkMax.class);

        lowerSpark.enableSoftLimit(CANSparkMax.SoftLimitDirection.kForward, true);
        lowerSpark.enableSoftLimit(CANSparkMax.SoftLimitDirection.kReverse, true);
        upperSpark.enableSoftLimit(CANSparkMax.SoftLimitDirection.kForward, true);
        upperSpark.enableSoftLimit(CANSparkMax.SoftLimitDirection.kReverse, true);

        lowerSpark.setSoftLimit(CANSparkMax.SoftLimitDirection.kForward, (float) 27.3);
        lowerSpark.setSoftLimit(CANSparkMax.SoftLimitDirection.kReverse, (float) -13.69);

        upperSpark.setSoftLimit(CANSparkMax.SoftLimitDirection.kForward, (float) 25.0);
        upperSpark.setSoftLimit(CANSparkMax.SoftLimitDirection.kReverse, (float) -90.0);

        Debuggable debug = self.generateDebugger();

        return new ArmControl(
                lower,
                upper,
                debug
        );
    }
}

