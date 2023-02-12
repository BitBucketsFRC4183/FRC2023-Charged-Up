package org.bitbuckets.arm;

import edu.wpi.first.wpilibj.smartdashboard.MechanismLigament2d;
import org.bitbuckets.arm.kinematics.InverseKinematics;
import org.bitbuckets.lib.hardware.IMotorController;
import org.bitbuckets.lib.log.Debuggable;


public class ArmControl {


    final IMotorController lowerJoint1;
    final IMotorController lowerJoint2;
    final IMotorController upperJoint;
    final Debuggable debuggable;


    // How do set up IMotorController and IEncoder so that lowerJoint == lowerEncoder

    public ArmControl(IMotorController lowerJoint1, IMotorController lowerJoint2, IMotorController upperJoint, Debuggable debuggable) {
        this.lowerJoint1 = lowerJoint1;
        this.lowerJoint2 = lowerJoint2;
        this.upperJoint = upperJoint;
        this.debuggable = debuggable;
    }

    public void calibrateLowerArm() {
        lowerJoint1.forceOffset(convertMechanismRotationtoRawRotation_lowerJoint(convertDegreesToRotation(0)));
        lowerJoint2.forceOffset(convertMechanismRotationtoRawRotation_lowerJoint(convertDegreesToRotation(0)));
    }

    public void calibrateUpperArm() {
        upperJoint.forceOffset(convertMechanismRotationtoRawRotation_upperJoint(convertDegreesToRotation(0)));
    }

    public void manuallyMoveLowerArm(double percentOutput) {

//        double lowerRotation = gearRatio * percentOutput / 360;
//        lowerJoint.getPIDController().setReference(lowerRotation, CANSparkMax.ControlType.kPosition);
        //test if lower arm moves with outputs
        lowerJoint1.moveAtPercent(percentOutput * ArmConstants.CONTROL_JOINT_OUTPUT);
        lowerJoint2.moveAtPercent(percentOutput * ArmConstants.CONTROL_JOINT_OUTPUT);
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
        return Math.abs(lowerJoint1.getError_mechanismRotations()) < delta && Math.abs(upperJoint.getError_mechanismRotations()) < delta;
    }


    // Make sure to change/tune lowerAngle_degrees and upperAngle_degrees for each position
    public void stopArmMotors() {
        lowerJoint1.moveAtPercent(0);
        lowerJoint2.moveAtPercent(0);
        upperJoint.moveAtPercent(0);
    }

    public void goToCalibrationPosition() {

        lowerJoint1.moveToPosition_mechanismRotations(0);
        lowerJoint2.moveToPosition_mechanismRotations(0);
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
            lowerJoint1.moveToPosition_mechanismRotations(convertDegreesToRotation(lowerAngle_degrees));
            lowerJoint2.moveToPosition_mechanismRotations(convertDegreesToRotation(lowerAngle_degrees));
            upperJoint.moveToPosition_mechanismRotations(convertDegreesToRotation(upperAngle_degrees));

        }

    }

    public void storeArm() {
        /*
        InverseKinematics store = new InverseKinematics(ArmConstants.STORAGE_X, ArmConstants.STORAGE_Y);
        double lowerAngle_degrees = store.getLowerJoint_degrees();
        double upperAngle_degrees = store.getUpperJoint_degrees();




        debuggable.log("lower-kinematics", convertDegreesToRotation(lowerAngle_degrees));
        debuggable.log("upper-kinematics", convertDegreesToRotation(upperAngle_degrees));

        //finding NaN errors
        if (isReachable(lowerAngle_degrees, upperAngle_degrees)) {
            lowerJoint1.moveToPosition_mechanismRotations(convertDegreesToRotation(lowerAngle_degrees));
            lowerJoint2.moveToPosition_mechanismRotations(convertDegreesToRotation(lowerAngle_degrees));
            upperJoint.moveToPosition_mechanismRotations(convertDegreesToRotation(upperAngle_degrees));
        }

         */
        if (isReachable(0, 10)) {
            lowerJoint1.moveToPosition_mechanismRotations(convertDegreesToRotation(45));
            lowerJoint2.moveToPosition_mechanismRotations(convertDegreesToRotation(45));
            upperJoint.moveToPosition_mechanismRotations(convertDegreesToRotation(90));
        }
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
            lowerJoint1.moveToPosition_mechanismRotations(convertDegreesToRotation(lowerAngle_degrees));
            lowerJoint2.moveToPosition_mechanismRotations(convertDegreesToRotation(lowerAngle_degrees));
            upperJoint.moveToPosition_mechanismRotations(convertDegreesToRotation(upperAngle_degrees));
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
            lowerJoint1.moveToPosition_mechanismRotations(convertDegreesToRotation(lowerAngle_degrees));
            lowerJoint2.moveToPosition_mechanismRotations(convertDegreesToRotation(lowerAngle_degrees));
            upperJoint.moveToPosition_mechanismRotations(convertDegreesToRotation(upperAngle_degrees));
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
            lowerJoint1.moveToPosition_mechanismRotations(convertDegreesToRotation(lowerAngle_degrees));
            lowerJoint2.moveToPosition_mechanismRotations(convertDegreesToRotation(lowerAngle_degrees));
            upperJoint.moveToPosition_mechanismRotations(convertDegreesToRotation(upperAngle_degrees));
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
            lowerJoint1.moveToPosition_mechanismRotations(convertDegreesToRotation(lowerAngle_degrees));
            lowerJoint2.moveToPosition_mechanismRotations(convertDegreesToRotation(lowerAngle_degrees));
            upperJoint.moveToPosition_mechanismRotations(convertDegreesToRotation(upperAngle_degrees));
        }
    }


}


