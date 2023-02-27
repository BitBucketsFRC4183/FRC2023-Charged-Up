package org.bitbuckets.gripper;

import config.Arm;
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
        gripper.moveToPosition_mechanismRotations(Arm.GRIPPER_SETPOINT_MOTOR_ROTATIONS);
    }

    public void closeGripper(){
        gripper.moveAtPercent(0);
    }

    public void manuallyCloseGripper(){
        gripper.moveToPosition_mechanismRotations(Arm.CLOSE_DA_GRIPPA);
    }
}
