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

    final ILoggable<Double> findLowerAngleKinematics;
    final ILoggable<Double> findUpperAngleKinematics;



    // How do set up IMotorController and IEncoder so that lowerJoint == lowerEncoder


    public ArmControl(IMotorController lowerJoint, IMotorController upperJoint, ILoggable<Boolean> isArmOutOfReach, ILoggable<Double> convertRawToMechanism, ILoggable<Double> convertLowerRawToMechanism, ILoggable<Double> findLowerAngle, ILoggable<Double> findUpperAngle) {
        this.lowerJoint = lowerJoint;
        this.upperJoint = upperJoint;



        this.isArmOutOfReach = isArmOutOfReach;

        this.findLowerAngleKinematics = findLowerAngle;
        this.findUpperAngleKinematics = findUpperAngle;

        findLowerAngle.log(0.);
        findUpperAngle.log(0.);

        isArmOutOfReach.log(false);

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


    public boolean isReachable(double lowerAngle, double upperAngle)
    {
        if (Double.isNaN(lowerAngle) || Double.isNaN(upperAngle))
        {
            isArmOutOfReach.log(false);
            return false;
        }
        else {
            isArmOutOfReach.log(true);
            return true;
        }
    }

    public boolean isErrorSmallEnough(double delta){
        //might change delta later
        return lowerJoint.getError() < delta && upperJoint.getError() < delta;
    }

    // Make sure to change/tune lowerAngle and upperAngle for each position

    // Press X
    public boolean humanIntake() {
        //Need inverse kinematics
        double lowerAngle = 0;
        double upperAngle = 0;
        moveLowerArmToPosition_DEGREES(lowerAngle);
        moveUpperArmToPosition_DEGREES(upperAngle);

    }

    public boolean storeArm() {
        InverseKinematics store = new InverseKinematics(ArmConstants.STORAGE_X, ArmConstants.STORAGE_Y);
        double lowerAngle = store.getLowerJointAngle();
        double upperAngle = store.getUpperJointAngle();

        findLowerAngleKinematics.log(Math.toDegrees(lowerAngle));
        findUpperAngleKinematics.log(Math.toDegrees(upperAngle));

        //finding NaN errors
        if (isReachable(lowerAngle, upperAngle))
        {
            moveLowerArmToPosition_DEGREES(Math.toDegrees(lowerAngle));
            moveUpperArmToPosition_DEGREES(Math.toDegrees(upperAngle));
            return isErrorSmallEnough(3.69);
        }
        return false;

    }

    public boolean prepareArm() {
        InverseKinematics prepare = new InverseKinematics(ArmConstants.PREPARE_X, ArmConstants.PREPARE_Y);
        double lowerAngle = prepare.getLowerJointAngle();
        double upperAngle = prepare.getUpperJointAngle();

        findLowerAngleKinematics.log(Math.toDegrees(lowerAngle));
        findUpperAngleKinematics.log(Math.toDegrees(upperAngle));

        //finding NaN errors
        if (isReachable(lowerAngle, upperAngle))
        {
            moveLowerArmToPosition_DEGREES(Math.toDegrees(lowerAngle));
            moveUpperArmToPosition_DEGREES(Math.toDegrees(upperAngle));
        }
        return isErrorSmallEnough(3.69);
    }



    // Press A
    public boolean scoreMid() {
        //Need inverse kinematics

        // pass x position and y position as parameters to the inverse kinematics constructor

        /* DIFFERENT INVERSEKINEMATICS TESTS
         * InverseKinematics midNode = new InverseKinematics(-1.535 * Math.sin(Math.PI/4),1.535 * Math.sin(Math.PI/4)); works
         * InverseKinematics midNode = new InverseKinematics(0.85, 0.95); works
         * InverseKinematics midNode = new InverseKinematics(1.169, 0.35); works
         */

        InverseKinematics midNode = new InverseKinematics(ArmConstants.MID_NODE_X, ArmConstants.MID_NODE_Y);
        double lowerAngle = midNode.getLowerJointAngle();
        double upperAngle = midNode.getUpperJointAngle();

        findLowerAngleKinematics.log(Math.toDegrees(lowerAngle));
        findUpperAngleKinematics.log(Math.toDegrees(upperAngle));

        //finding NaN errors
        if (isReachable(lowerAngle, upperAngle))
        {
            moveLowerArmToPosition_DEGREES(Math.toDegrees(lowerAngle));
            moveUpperArmToPosition_DEGREES(Math.toDegrees(upperAngle));
            return isErrorSmallEnough(3.69);
        }

        return false;


    }

    // Press B
    public boolean scoreHigh() {
        InverseKinematics highNode = new InverseKinematics(ArmConstants.HIGH_NODE_X, ArmConstants.HIGH_NODE_Y);
        double lowerAngle = highNode.getLowerJointAngle();
        double upperAngle = highNode.getUpperJointAngle();

        findLowerAngleKinematics.log(Math.toDegrees(lowerAngle));
        findUpperAngleKinematics.log(Math.toDegrees(upperAngle));

        //finding NaN errors
        if (isReachable(lowerAngle, upperAngle))
        {
            moveLowerArmToPosition_DEGREES(Math.toDegrees(lowerAngle));
            moveUpperArmToPosition_DEGREES(Math.toDegrees(upperAngle));
            return isErrorSmallEnough(3.69);
        }

        return false;

    }


}
