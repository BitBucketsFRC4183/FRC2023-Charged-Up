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
        lowerJoint.forceOffset(convertMechanismRotationtoRawRotation_lowerJoint(convertDegreesToRotation(0)));

    }

    public void calibrateUpperArm() {
        System.out.println("Calibrated upper arm!");
        upperJoint.forceOffset(convertMechanismRotationtoRawRotation_upperJoint(convertDegreesToRotation(0)));

    }

    public void manuallyMoveLowerArm(double percentOutput) {

//        double lowerRotation = gearRatio * percentOutput / 360;
//        lowerJoint.getPIDController().setReference(lowerRotation, CANSparkMax.ControlType.kPosition);
//
        //test if lower arm moves with outputs
        lowerJoint.moveAtPercent(percentOutput * ArmConstants.CONTROL_JOINT_OUTPUT);
    }


    //sets angular position of the upper joint on the arm
    public void manuallyMoveUpperArm(double percentOutput) {

        upperJoint.moveAtPercent(percentOutput * 3);
    }

    public void moveLowerArmToPosition_DEGREES(double angle)
    {
        lowerJoint.moveToPosition(convertMechanismRotationtoRawRotation_lowerJoint(angle));
    }

    public void moveUpperArmToPosition_DEGREES(double angle)
    {
        upperJoint.moveToPosition(convertMechanismRotationtoRawRotation_upperJoint(angle));
    }


    public double convertDegreesToRotation(double degrees) {
        return degrees / 360.;
    }

    public double convertMechanismRotationtoRawRotation_lowerJoint(double mechanismRotation)
    {
        return mechanismRotation * ArmConstants.lowerArmGearRatio;
    }

    public double convertMechanismRotationtoRawRotation_upperJoint(double mechanismRotation) {
        return mechanismRotation * ArmConstants.upperArmGearRatio;
    }

    public void moveToIntakePos() {

    }

    // Press Y
    public void moveToLowPos() {

        moveLowerArmToPosition_DEGREES(1);

    }

    // Press X
    public void moveToMidPos() {

    }

    // Press B
    public void movetoHighPos() {

    }


    public void stopLower() {
        lowerJoint.moveAtPercent(0);
    }

    public void stopUpper() {
        upperJoint.moveAtPercent(0);
    }

}