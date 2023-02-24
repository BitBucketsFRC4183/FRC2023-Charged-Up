package org.bitbuckets.gripper;

import org.bitbuckets.lib.hardware.IMotorController;
import org.bitbuckets.lib.log.Debuggable;

public class GripperControl {

    final Debuggable debuggable;
    final IMotorController gripper;

    public GripperControl(Debuggable debuggable, IMotorController gripper) {
        this.debuggable = debuggable;
        this.gripper = gripper;
    }

    public void openGripper(){
        gripper.moveToPosition_mechanismRotations(GripperConstants.MOTOR_TO_GRIPPER_ROTATIONS);
    }

    public void closeGripper(){
        gripper.moveToPosition_mechanismRotations(0);
    }
}
