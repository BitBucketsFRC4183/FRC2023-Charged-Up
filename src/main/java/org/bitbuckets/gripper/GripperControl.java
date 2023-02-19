package org.bitbuckets.gripper;

import org.bitbuckets.lib.hardware.IMotorController;
import org.bitbuckets.lib.log.Debuggable;

public class GripperControl {

    final Debuggable debuggable;
    final IMotorController gripper;
    final GripperConstants gripperConstants;

    public GripperControl(Debuggable debuggable, IMotorController gripper, GripperConstants gripperConstants) {
        this.debuggable = debuggable;
        this.gripper = gripper;
        this.gripperConstants = gripperConstants;
    }

    public void openGripper(){
        gripper.moveToPosition_mechanismRotations(gripperConstants.MOTOR_TO_GRIPPER_ROTATIONS);
    }

    public void closeGripper(){
        gripper.moveToPosition_mechanismRotations(0);
    }
}
