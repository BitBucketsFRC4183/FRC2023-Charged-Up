package org.bitbuckets.arm;

import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.ProcessPath;
import org.bitbuckets.lib.hardware.IMotorController;
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
        return new ArmControl(
                lowerJoint.build(self.addChild("lower-joint")),
                upperJoint.build(self.addChild("upper-joint"))
        );
    }
}

