package org.bitbuckets.arm;

import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.ProcessPath;
import org.bitbuckets.lib.hardware.IMotorController;

public class ArmControlSetup implements ISetup<ArmControl> {

    // Lower Device ID = 9
    // Upper Device ID = 3
    final ISetup<IMotorController> lowerJoint;
    final ISetup<IMotorController> upperJoint;


    public ArmControlSetup(ISetup<IMotorController> lowerJoint, ISetup<IMotorController> upperJoint) {
        this.lowerJoint = lowerJoint;
        this.upperJoint = upperJoint;

    }

    @Override
    public ArmControl build(ProcessPath path) {

        ArmControl armControl = new ArmControl(
                lowerJoint.build(path.addChild("lower-joint")),
                upperJoint.build(path.addChild("upper-joint"))

        );
        return armControl;
    }
}

