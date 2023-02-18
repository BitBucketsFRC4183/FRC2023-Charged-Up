package org.bitbuckets.gripper;

import org.bitbuckets.lib.hardware.IMotorController;
import org.bitbuckets.lib.log.Debuggable;
import org.bitbuckets.lib.tune.IValueTuner;

public class GripperControl {

    final Debuggable debuggable;
    final IValueTuner<Double> motorToGripperRotations;

    final IMotorController gripper;

    public GripperControl(Debuggable debuggable, IValueTuner<Double> motorToGripperRotations, IMotorController gripper) {
        this.debuggable = debuggable;
        this.motorToGripperRotations = motorToGripperRotations;
        this.gripper = gripper;
    }

    public void openGripper(){
        debuggable.log("tuner-val", motorToGripperRotations.readValue());
        gripper.moveToPosition_mechanismRotations(motorToGripperRotations.readValue());
    }

    public void closeGripper(){
        gripper.moveToPosition_mechanismRotations(0);
    }
}
