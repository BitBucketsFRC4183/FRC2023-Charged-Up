package org.bitbuckets.gripper;

import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.ProcessPath;
import org.bitbuckets.lib.util.MockingUtil;
import org.bitbuckets.lib.vendor.spark.SparkSetup;

public class GripperSubsystemSetup implements ISetup<GripperSubsystem> {

    final boolean isEnabled;

    public GripperSubsystemSetup(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    @Override
    public GripperSubsystem build(ProcessPath self) {
        if (!isEnabled) {
            return MockingUtil.buddy(GripperSubsystem.class);
        }
        GripperControlSetup gripperControlSetup = new GripperControlSetup(new SparkSetup(12, GripperConstants.GRIPPER_CONFIG, GripperConstants.GRIPPER_PID));
        GripperControl gripperControl = gripperControlSetup.build(self.addChild("gripper-control"));

        return new GripperSubsystem(gripperControl);
    }
}
