package org.bitbuckets.arm;

import org.bitbuckets.lib.hardware.IMotorController;
import org.bitbuckets.lib.log.ILoggable;


public class ArmControl {


    final IMotorController lowerJoint;
    final IMotorController upperJoint;
    final ILoggable<Boolean> isArmOutOfReach;

    // convertLowerRawToMechanism and convertUpperRawToMechanism are 0 by default
    final ILoggable<Double> convertLowerRawToMechanism;
    final ILoggable<Double> convertUpperRawToMechanism;


    // How do set up IMotorController and IEncoder so that lowerJoint == lowerEncoder


    public ArmControl(IMotorController lowerJoint, IMotorController upperJoint, ILoggable<Boolean> isArmOutOfReach, ILoggable<Double> convertRawToMechanism, ILoggable<Double> convertLowerRawToMechanism) {
        this.lowerJoint = lowerJoint;
        this.upperJoint = upperJoint;



        this.isArmOutOfReach = isArmOutOfReach;
        this.convertLowerRawToMechanism = convertLowerRawToMechanism;
        this.convertUpperRawToMechanism = convertRawToMechanism;
        convertLowerRawToMechanism.log(0.);
        convertUpperRawToMechanism.log(0.);
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
        double lowerConverted = convertMechanismRotationtoRawRotation_upperJoint(angle / 360.);
        convertLowerRawToMechanism.log(lowerConverted);
        lowerJoint.moveToPosition(lowerConverted);
    }

    public void moveUpperArmToPosition_DEGREES(double angle) {

        double upperConverted = convertMechanismRotationtoRawRotation_upperJoint(angle / 360.);
        convertUpperRawToMechanism.log(upperConverted);
        upperJoint.moveToPosition(upperConverted);
    }


    public double convertDegreesToRotation(double degrees) {
        return degrees / 360.;
    }

    public double convertMechanismRotationtoRawRotation_lowerJoint(double mechanismRotation) {
        return mechanismRotation / ArmConstants.LOWER_ARM_GEAR_RATIO;
    }

    public double convertMechanismRotationtoRawRotation_upperJoint(double mechanismRotation) {
        return mechanismRotation / ArmConstants.UPPER_ARM_GEAR_RATIO;
    }

    // Make sure to change/tune lowerAngle and upperAngle for each position

    // Press X
    public void intakeHumanPlayer() {
        //Need inverse kinematics
        double lowerAngle = 29.69;
        double upperAngle = -29.69;
        moveLowerArmToPosition_DEGREES(lowerAngle);
        moveUpperArmToPosition_DEGREES(upperAngle);

    }

    // Press Y
    public void intakeGround() {
        //Need inverse kinematics
        double lowerAngle = -29.69;
        double upperAngle = 29.69;
        moveLowerArmToPosition_DEGREES(lowerAngle);
        moveUpperArmToPosition_DEGREES(upperAngle);

    }

    // Press A
    public void scoreMid() {
        //Need inverse kinematics

        // pass x position and y position as parameters to the inverse kinematics constructor
        InverseKinematics midNode = new InverseKinematics(1.085,1.085);
        double lowerAngle = midNode.getLowerJointAngle();
        double upperAngle = midNode.getUpperJointAngle();

        //finding NaN errors
        if (lowerAngle != Double.NaN && upperAngle != Double.NaN)
        {
            isArmOutOfReach.log(true);
            moveLowerArmToPosition_DEGREES(Math.toDegrees(lowerAngle));
            moveUpperArmToPosition_DEGREES(Math.toDegrees(upperAngle));
        }
        else{
            isArmOutOfReach.log(false);
        }


    }

    // Press B
    public void scoreHigh() {

        //Need inverse kinematics
        double lowerAngle = 0;
        double upperAngle = 0;
        moveLowerArmToPosition_DEGREES(lowerAngle);
        moveUpperArmToPosition_DEGREES(upperAngle);

    }
}
