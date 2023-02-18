package org.bitbuckets.gripper;

import org.bitbuckets.lib.hardware.IMotorController;
import org.bitbuckets.lib.tune.IValueTuner;
import org.bitbuckets.lib.vendor.spark.SparkSetup;

public class GripperControl {

    final IValueTuner<Double> motorToGripperRotations;

    final IMotorController gripper;

    public GripperControl(IValueTuner<Double> motorToGripperRotations, IMotorController gripper) {
        this.motorToGripperRotations = motorToGripperRotations;
        this.gripper = gripper;
    }

    public void openGripper(){
        gripper.moveToPosition_mechanismRotations(motorToGripperRotations.readValue());
    }

    public void closeGripper(){
        gripper.moveToPosition_mechanismRotations(0);
    }
}
