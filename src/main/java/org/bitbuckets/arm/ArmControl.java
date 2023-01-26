package org.bitbuckets.arm;

import org.bitbuckets.lib.hardware.IMotorController;


public class ArmControl {


    final IMotorController lowerJoint;
    final IMotorController upperJoint;


    // How do set up IMotorController and IEncoder so that lowerJoint == lowerEncoder


    public ArmControl(IMotorController lowerJoint, IMotorController upperJoint) {
        this.lowerJoint = lowerJoint;
        this.upperJoint = upperJoint;


    }

    public void calibrateLowerArm() {
        System.out.println("Calibrated lower arm!");
        lowerJoint.forceOffset(convertDegreesToRotations(-90));

    }

    public void calibrateUpperArm() {
        System.out.println("Calibrated upper arm!");
        upperJoint.forceOffset(convertDegreesToRotations(0));

    }

    // motors output in rotations converted into degrees


    public void moveLowerArm(double percentOutput) {

//        double lowerRotation = gearRatio * percentOutput / 360;
//        lowerJoint.getPIDController().setReference(lowerRotation, CANSparkMax.ControlType.kPosition);
//
        //test if lower arm moves with outputs
        lowerJoint.moveAtPercent(percentOutput * ArmConstants.CONTROL_JOINT_OUTPUT);
    }


    //sets angular position of the upper joint on the arm
    public void moveUpperArm(double percentOutput) {

        upperJoint.moveAtPercent(percentOutput * ArmConstants.CONTROL_JOINT_OUTPUT);

    }

    public double convertDegreesToRotations(double degrees) {
        return degrees / 360.;
    }


    public void moveToLowPos() {

    }

    public void moveToMidPos() {

    }

    public void movetoHighPos() {

    }


    public void stopLower() {
        lowerJoint.moveAtPercent(0);
    }

    public void stopUpper() {
        upperJoint.moveAtPercent(0);
    }

}