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
        lowerJoint.forceOffset(convertMechanismRotationtoRawRotation_lowerJoint(convertDegreesToRotation(0)));
    }

    public void calibrateUpperArm() {
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

        upperJoint.moveAtPercent(percentOutput * ArmConstants.CONTROL_JOINT_OUTPUT);
    }

    public void moveLowerArmToPosition_DEGREES(double angle) {
        lowerJoint.moveToPosition(convertMechanismRotationtoRawRotation_lowerJoint(angle / 360));
    }

    public void moveUpperArmToPosition_DEGREES(double angle) {
        upperJoint.moveToPosition(convertMechanismRotationtoRawRotation_upperJoint(angle / 360));
    }


    public double convertDegreesToRotation(double degrees) {
        return degrees / 360.;
    }

    public double convertMechanismRotationtoRawRotation_lowerJoint(double mechanismRotation) {
        return mechanismRotation * ArmConstants.LOWER_ARM_GEAR_RATIO;
    }

    public double convertMechanismRotationtoRawRotation_upperJoint(double mechanismRotation) {
        return mechanismRotation * ArmConstants.UPPER_ARM_GEAR_RATIO;
    }

    // Make sure to change/tune lowerAngle and upperAngle for each position

    // Press X
    public void intakeHumanPlayer() {
        //Need inverse kinematics
        double lowerAngle = 0;
        double upperAngle = 0;
        moveLowerArmToPosition_DEGREES(lowerAngle);
        moveUpperArmToPosition_DEGREES(upperAngle);

    }

    // Press Y
    public void intakeGround() {
        //Need inverse kinematics
        double lowerAngle = 0;
        double upperAngle = 0;
        moveLowerArmToPosition_DEGREES(lowerAngle);
        moveUpperArmToPosition_DEGREES(upperAngle);
    }

    public void scoreMid() {
        //Need inverse kinematics

        // pass x position and y position as parameters to the inverse kinematics constructor
        double lowerAngle = 0;
        double upperAngle = 0;
        moveLowerArmToPosition_DEGREES(lowerAngle);
        moveUpperArmToPosition_DEGREES(upperAngle);

    }

    // Press B
    public void scoreHigh() {

        // Press A
        //Need inverse kinematics
        double lowerAngle = 0;
        double upperAngle = 0;
        moveLowerArmToPosition_DEGREES(lowerAngle);
        moveUpperArmToPosition_DEGREES(upperAngle);

    }
}
