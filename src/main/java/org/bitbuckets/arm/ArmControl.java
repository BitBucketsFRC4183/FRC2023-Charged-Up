package org.bitbuckets.arm;

import edu.wpi.first.math.geometry.Translation2d;
import org.bitbuckets.arm.kinematics.InverseKinematics;
import org.bitbuckets.lib.control.IPIDCalculator;
import org.bitbuckets.lib.hardware.IMotorController;
import org.bitbuckets.lib.log.Debuggable;


public class ArmControl {


    final IMotorController lowerJoint1;

    final IMotorController upperJoint;
    final Debuggable debuggable;

    final IPIDCalculator lowerJointPID;
    final IPIDCalculator upperJointPID;

    public ArmControl(IMotorController lowerJoint1, IMotorController upperJoint, Debuggable debuggable, IPIDCalculator lowerJointPID, IPIDCalculator upperJointPID) {
        this.lowerJoint1 = lowerJoint1;
        this.upperJoint = upperJoint;
        this.debuggable = debuggable;
        this.lowerJointPID = lowerJointPID;
        this.upperJointPID = upperJointPID;
    }

    public void calibrateLowerArm() {
        lowerJoint1.forceOffset(convertMechanismRotationtoRawRotation_lowerJoint(convertDegreesToRotation(0)));
    }

    public void calibrateUpperArm() {
        upperJoint.forceOffset(convertMechanismRotationtoRawRotation_upperJoint(convertDegreesToRotation(0)));
    }

    public void manuallyMoveLowerArm(double percentOutput) {

//        double lowerRotation = gearRatio * percentOutput / 360;
//        lowerJoint.getPIDController().setReference(lowerRotation, CANSparkMax.ControlType.kPosition);
        //test if lower arm moves with outputs
        lowerJoint1.moveAtPercent(percentOutput * ArmConstants.CONTROL_JOINT_OUTPUT);
    }


    public void manuallyMoveUpperArm(double percentOutput) {
        upperJoint.moveAtPercent(percentOutput * ArmConstants.CONTROL_JOINT_OUTPUT);
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


    public boolean isReachable(double lowerAngle_degrees, double upperAngle_degrees) {
        debuggable.log("lower-angle", lowerAngle_degrees);
        debuggable.log("upper-angle", upperAngle_degrees);

        if (Double.isNaN(lowerAngle_degrees) || Double.isNaN(upperAngle_degrees)) {
            debuggable.log("is-reach", false);
            return false;
        } else {
            debuggable.log("is-reach", true);
            return true;
        }
    }

    // may change delta later
    public boolean isErrorSmallEnough(double delta) {
        return false;

        //debuggable.log("lowerJoint Error", Math.abs(lowerJoint1.getError_mechanismRotations()));
        //debuggable.log("upperJoint Error", Math.abs(Math.abs(upperJoint.getError_mechanismRotations())));

        //return Math.abs(lowerJoint1.getError_mechanismRotations()) < delta && Math.abs(upperJoint.getError_mechanismRotations()) < delta;
    }


    // Make sure to change/tune lowerAngle_degrees and upperAngle_degrees for each position
    public void stopArmMotors() {
        lowerJoint1.moveAtPercent(0);
        upperJoint.moveAtPercent(0);
    }

    public void goToCalibrationPosition() {
        lowerJoint1.moveToPosition_mechanismRotations(0);
        upperJoint.moveToPosition_mechanismRotations(0);
    }

    // Press X
    public void humanIntake() {
        //Need inverse kinematics

        InverseKinematics humanPlayer = new InverseKinematics(ArmConstants.HUMAN_INTAKE_X, ArmConstants.HUMAN_INTAKE_Y);
        double lowerAngle_degrees = humanPlayer.getLowerJoint_degrees();
        double upperAngle_degrees = humanPlayer.getUpperJoint_degrees();

        debuggable.log("lower-kinematics", lowerAngle_degrees);
        debuggable.log("upper-kinematics", upperAngle_degrees);

        //finding NaN errors
        if (isReachable(lowerAngle_degrees, upperAngle_degrees)) {

            lowerJoint1.moveAtVoltage(lowerJointPID.calculateNext(lowerJoint1.getMechanismPositionAccum_rot(), convertDegreesToRotation(lowerAngle_degrees)));
            upperJoint.moveAtVoltage(upperJointPID.calculateNext(upperJoint.getMechanismPositionAccum_rot(), convertDegreesToRotation(upperAngle_degrees)));

            /*
            lowerJoint1.moveToPosition_mechanismRotations(convertDegreesToRotation(lowerAngle_degrees));
            upperJoint.moveToPosition_mechanismRotations(convertDegreesToRotation(upperAngle_degrees));

             */

        }

    }

    public void storeArm() {

        InverseKinematics store = new InverseKinematics(ArmConstants.STORAGE_X, ArmConstants.STORAGE_Y);
        double lowerAngle_degrees = store.getLowerJoint_degrees();
        double upperAngle_degrees = store.getUpperJoint_degrees();


        debuggable.log("lower-kinematics", convertDegreesToRotation(lowerAngle_degrees));
        debuggable.log("upper-kinematics", convertDegreesToRotation(upperAngle_degrees));

        //finding NaN errors
        if (isReachable(lowerAngle_degrees, upperAngle_degrees)) {
            debuggable.log("voltage", upperJointPID.calculateNext(upperJoint.getMechanismPositionAccum_rot(), convertDegreesToRotation(upperAngle_degrees)));
            lowerJoint1.moveAtVoltage(lowerJointPID.calculateNext(lowerJoint1.getMechanismPositionAccum_rot(), convertDegreesToRotation(lowerAngle_degrees)));
            upperJoint.moveAtVoltage(upperJointPID.calculateNext(upperJoint.getMechanismPositionAccum_rot(), convertDegreesToRotation(upperAngle_degrees)));

        }

         /*
        if (isReachable(0, 10)) {
            lowerJoint1.moveToPosition_mechanismRotations(convertDegreesToRotation(45));
            lowerJoint2.moveToPosition_mechanismRotations(convertDegreesToRotation(45));
            upperJoint.moveToPosition_mechanismRotations(convertDegreesToRotation(90));
        }

          */
    }

    public void moveToSetpointOnly(double lowerAngle_degrees, double upperAngle_degrees) {
        lowerJoint1.moveAtVoltage(lowerJointPID.calculateNext(lowerJoint1.getMechanismPositionAccum_rot(), convertDegreesToRotation(lowerAngle_degrees)));
        upperJoint.moveAtVoltage(upperJointPID.calculateNext(upperJoint.getMechanismPositionAccum_rot(), convertDegreesToRotation(upperAngle_degrees)));

    }

    public void prepareArm() {
        debuggable.log("arm-is-called", true);

        InverseKinematics prepare = new InverseKinematics(ArmConstants.PREPARE_X, ArmConstants.PREPARE_Y);
        double lowerAngle_degrees = prepare.getLowerJoint_degrees();
        double upperAngle_degrees = prepare.getUpperJoint_degrees();

        debuggable.log("lower-kinematics", lowerAngle_degrees);
        debuggable.log("upper-kinematics", upperAngle_degrees);

        //finding NaN errors
        if (isReachable(lowerAngle_degrees, upperAngle_degrees)) {

            //lowerJoint1.moveAtVoltage(ffLowerArmCalculator.calculateNext(lowerJoint1.getMechanismPositionAccum_rot(), convertDegreesToRotation(lowerAngle_degrees)));

            lowerJoint1.moveAtVoltage(lowerJointPID.calculateNext(lowerJoint1.getMechanismPositionAccum_rot(), convertDegreesToRotation(lowerAngle_degrees)));
            upperJoint.moveAtVoltage(upperJointPID.calculateNext(upperJoint.getMechanismPositionAccum_rot(), convertDegreesToRotation(upperAngle_degrees)));


            //waiting for ff to be finished
        }
    }

    public void scoreLow() {
        InverseKinematics lowNode = new InverseKinematics(ArmConstants.LOW_NODE_X, ArmConstants.LOW_NODE_Y);
        double lowerAngle_degrees = lowNode.getLowerJoint_degrees();
        double upperAngle_degrees = lowNode.getUpperJoint_degrees();

        debuggable.log("lower-kinematics", lowerAngle_degrees);
        debuggable.log("upper-kinematics", upperAngle_degrees);

        //finding NaN errors
        if (isReachable(lowerAngle_degrees, upperAngle_degrees)) {

            lowerJoint1.moveAtVoltage(lowerJointPID.calculateNext(lowerJoint1.getMechanismPositionAccum_rot(), convertDegreesToRotation(lowerAngle_degrees)));
            upperJoint.moveAtVoltage(upperJointPID.calculateNext(upperJoint.getMechanismPositionAccum_rot(), convertDegreesToRotation(upperAngle_degrees)));


            /*
            lowerJoint1.moveAtVoltage(ffLowerArmCalculator.calculateNext(lowerJoint1.getMechanismPositionAccum_rot(), convertDegreesToRotation(lowerAngle_degrees)));
            lowerJoint2.moveAtVoltage(ffLowerArmCalculator.calculateNext(lowerJoint2.getMechanismPositionAccum_rot(), convertDegreesToRotation(lowerAngle_degrees)));
            upperJoint.moveAtVoltage(ffUpperArmCalculator.calculateNext(upperJoint.getMechanismPositionAccum_rot(), convertDegreesToRotation(upperAngle_degrees)));
             */
        }


    }


    // Press A
    public void scoreMid() {
        //Need inverse kinematics

        // pass x position and y position as parameters to the inverse kinematics constructor

        /* DIFFERENT INVERSEKINEMATICS TESTS
         * InverseKinematics midNode = new InverseKinematics(-1.535 * Math.sin(Math.PI/4),1.535 * Math.sin(Math.PI/4)); works
         * InverseKinematics midNode = new InverseKinematics(0.85, 0.95); works
         * InverseKinematics midNode = new InverseKinematics(1.169, 0.35); works
         */


        InverseKinematics midNode = new InverseKinematics(ArmConstants.MID_NODE_X, ArmConstants.MID_NODE_Y);
        double lowerAngle_degrees = midNode.getLowerJoint_degrees();
        double upperAngle_degrees = midNode.getUpperJoint_degrees();

        debuggable.log("lower-kinematics", lowerAngle_degrees);
        debuggable.log("upper-kinematics", upperAngle_degrees);

        //finding NaN errors
        if (isReachable(lowerAngle_degrees, upperAngle_degrees)) {

            lowerJoint1.moveAtVoltage(lowerJointPID.calculateNext(lowerJoint1.getMechanismPositionAccum_rot(), convertDegreesToRotation(lowerAngle_degrees)));
            upperJoint.moveAtVoltage(upperJointPID.calculateNext(upperJoint.getMechanismPositionAccum_rot(), convertDegreesToRotation(upperAngle_degrees)));



            /*
            lowerJoint1.moveAtVoltage(ffLowerArmCalculator.calculateNext(lowerJoint1.getMechanismPositionAccum_rot(), convertDegreesToRotation(lowerAngle_degrees)));
            lowerJoint2.moveAtVoltage(ffLowerArmCalculator.calculateNext(lowerJoint2.getMechanismPositionAccum_rot(), convertDegreesToRotation(lowerAngle_degrees)));
            upperJoint.moveAtVoltage(ffUpperArmCalculator.calculateNext(upperJoint.getMechanismPositionAccum_rot(), convertDegreesToRotation(upperAngle_degrees)));
             */
            //waiting for ff to be finished
        }
    }


    public void scoreHigh() {
        InverseKinematics highNode = new InverseKinematics(ArmConstants.HIGH_NODE_X, ArmConstants.HIGH_NODE_Y);
        double lowerAngle_degrees = highNode.getLowerJoint_degrees();
        double upperAngle_degrees = highNode.getUpperJoint_degrees();

        debuggable.log("lower-kinematics", lowerAngle_degrees);
        debuggable.log("upper-kinematics", upperAngle_degrees);

        //finding NaN errors
        if (isReachable(lowerAngle_degrees, upperAngle_degrees)) {

            lowerJoint1.moveAtVoltage(lowerJointPID.calculateNext(lowerJoint1.getMechanismPositionAccum_rot(), convertDegreesToRotation(lowerAngle_degrees)));
            upperJoint.moveAtVoltage(upperJointPID.calculateNext(upperJoint.getMechanismPositionAccum_rot(), convertDegreesToRotation(upperAngle_degrees)));


            //lowerJoint1.moveAtVoltage(ffLowerArmCalculator.calculateNext(lowerJoint1.getMechanismPositionAccum_rot(), convertDegreesToRotation(lowerAngle_degrees)));
            //lowerJoint2.moveAtVoltage(ffLowerArmCalculator.calculateNext(lowerJoint2.getMechanismPositionAccum_rot(), convertDegreesToRotation(lowerAngle_degrees)));
            //upperJoint.moveAtVoltage(ffUpperArmCalculator.calculateNext(upperJoint.getMechanismPositionAccum_rot(), convertDegreesToRotation(upperAngle_degrees)));

        }
    }
    public void intakeGround(){
        InverseKinematics intakeGround = new InverseKinematics(ArmConstants.INTAKE_GROUND_X, ArmConstants.INTAKE_GROUND_Y);
        double lowerAngle_degrees = intakeGround.getLowerJoint_degrees();
        double upperAngle_degrees = intakeGround.getUpperJoint_degrees();

        debuggable.log("lower-kinematics", lowerAngle_degrees);
        debuggable.log("upper-kinematics", upperAngle_degrees);

        if (isReachable(lowerAngle_degrees, upperAngle_degrees)) {

            lowerJoint1.moveAtVoltage(lowerJointPID.calculateNext(lowerJoint1.getMechanismPositionAccum_rot(), convertDegreesToRotation(lowerAngle_degrees)));
            upperJoint.moveAtVoltage(upperJointPID.calculateNext(upperJoint.getMechanismPositionAccum_rot(), convertDegreesToRotation(upperAngle_degrees)));

        }

    }

    public void moveTo(Translation2d translation2d) {
        var kinematics = new InverseKinematics(translation2d.getX(), translation2d.getY());

        double lower = kinematics.getLowerJoint_degrees();
        double upper = kinematics.getUpperJoint_degrees();


    }

    //the next few functions are for the sim

    public void moveStraightUp(){
        InverseKinematics straightUp = new InverseKinematics(0, ArmConstants.LOWER_JOINT_LENGTH + ArmConstants.UPPER_JOINT_LENGTH);
        double lowerAngle_degrees = straightUp.getLowerJoint_degrees();
        double upperAngle_degrees = straightUp.getUpperJoint_degrees();

        debuggable.log("lower-kinematics", lowerAngle_degrees);
        debuggable.log("upper-kinematics", upperAngle_degrees);

        //finding NaN errors
        if (isReachable(lowerAngle_degrees, upperAngle_degrees)) {

            lowerJoint1.moveAtVoltage(lowerJointPID.calculateNext(lowerJoint1.getMechanismPositionAccum_rot(), convertDegreesToRotation(lowerAngle_degrees)));
            upperJoint.moveAtVoltage(upperJointPID.calculateNext(upperJoint.getMechanismPositionAccum_rot(), convertDegreesToRotation(upperAngle_degrees)));


            //lowerJoint1.moveAtVoltage(ffLowerArmCalculator.calculateNext(lowerJoint1.getMechanismPositionAccum_rot(), convertDegreesToRotation(lowerAngle_degrees)));
            //lowerJoint2.moveAtVoltage(ffLowerArmCalculator.calculateNext(lowerJoint2.getMechanismPositionAccum_rot(), convertDegreesToRotation(lowerAngle_degrees)));
            //upperJoint.moveAtVoltage(ffUpperArmCalculator.calculateNext(upperJoint.getMechanismPositionAccum_rot(), convertDegreesToRotation(upperAngle_degrees)));

        }
    }
    public void moveToZero(){
        InverseKinematics zero = new InverseKinematics(ArmConstants.LOWER_JOINT_LENGTH + ArmConstants.UPPER_JOINT_LENGTH, 0);
        double lowerAngle_degrees = zero.getLowerJoint_degrees();
        double upperAngle_degrees = zero.getUpperJoint_degrees();

        debuggable.log("lower-kinematics", lowerAngle_degrees);
        debuggable.log("upper-kinematics", upperAngle_degrees);

        //finding NaN errors
        if (isReachable(lowerAngle_degrees, upperAngle_degrees)) {

            lowerJoint1.moveAtVoltage(lowerJointPID.calculateNext(lowerJoint1.getMechanismPositionAccum_rot(), convertDegreesToRotation(lowerAngle_degrees)));
            upperJoint.moveAtVoltage(upperJointPID.calculateNext(upperJoint.getMechanismPositionAccum_rot(), convertDegreesToRotation(upperAngle_degrees)));


            //lowerJoint1.moveAtVoltage(ffLowerArmCalculator.calculateNext(lowerJoint1.getMechanismPositionAccum_rot(), convertDegreesToRotation(lowerAngle_degrees)));
            //lowerJoint2.moveAtVoltage(ffLowerArmCalculator.calculateNext(lowerJoint2.getMechanismPositionAccum_rot(), convertDegreesToRotation(lowerAngle_degrees)));
            //upperJoint.moveAtVoltage(ffUpperArmCalculator.calculateNext(upperJoint.getMechanismPositionAccum_rot(), convertDegreesToRotation(upperAngle_degrees)));

        }
    }
}


