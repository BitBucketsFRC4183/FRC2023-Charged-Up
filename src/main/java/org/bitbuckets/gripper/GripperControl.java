package org.bitbuckets.gripper;

import org.bitbuckets.lib.debug.IDebuggable;
import org.bitbuckets.lib.hardware.IMotorController;

public class GripperControl {

    final IDebuggable debuggable;
    final IMotorController gripper;

    public GripperControl(IDebuggable debuggable, IMotorController gripper) {
        this.debuggable = debuggable;
        this.gripper = gripper;
    }

    public void openGripper(){
        gripper.moveToPosition_mechanismRotations(GripperConstants.GRIPPER_SETPOINT_MOTOR_ROTATIONS);
    }

    public void closeGripper(){
        gripper.moveAtPercent(0);
    }

    public void manuallyCloseGripper(){
        gripper.moveToPosition_mechanismRotations(GripperConstants.CLOSE_DA_GRIPPA);
    }
}
