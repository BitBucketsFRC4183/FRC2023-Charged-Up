package org.bitbuckets.gripper;

import org.bitbuckets.arm.ArmFSM;

public class GripperSubsystem {

    final GripperControl gripperControl;

    public GripperSubsystem(GripperControl gripperControl) {
        this.gripperControl = gripperControl;
    }
}
