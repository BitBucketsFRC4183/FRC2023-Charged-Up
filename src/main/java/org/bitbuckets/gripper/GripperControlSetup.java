package org.bitbuckets.gripper;

import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.ProcessPath;
import org.bitbuckets.lib.hardware.IMotorController;
import org.bitbuckets.lib.tune.IValueTuner;

public class GripperControlSetup implements ISetup<GripperControl> {

    final ISetup<IMotorController> gripperJoint;

    public GripperControlSetup(ISetup<IMotorController> gripperJoint) {
        this.gripperJoint = gripperJoint;
    }

    @Override
    public GripperControl build(ProcessPath self) {
        var gripper = gripperJoint.build(self.addChild("gripper"));

        return new GripperControl(self.generateDebugger(), gripper);
    }
}
