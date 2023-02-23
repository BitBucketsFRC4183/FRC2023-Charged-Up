package org.bitbuckets.arm;

import org.bitbuckets.arm.kinematics.InverseKinematics;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class InverseKinematicsTest {

    @Test
    void getLowerJointAngle() {
        assertNotEquals(Double.NaN, new InverseKinematics(ArmConstants.HUMAN_INTAKE_X, ArmConstants.HUMAN_INTAKE_Y).getLowerJoint_degrees());
        assertNotEquals(Double.NaN, new InverseKinematics(ArmConstants.PREPARE_X, ArmConstants.PREPARE_Y).getLowerJoint_degrees());
        assertNotEquals(Double.NaN, new InverseKinematics(ArmConstants.STORAGE_X, ArmConstants.STORAGE_Y).getLowerJoint_degrees());
        assertNotEquals(Double.NaN, new InverseKinematics(ArmConstants.HIGH_NODE_X, ArmConstants.HIGH_NODE_Y).getLowerJoint_degrees());
        assertNotEquals(Double.NaN, new InverseKinematics(ArmConstants.MID_NODE_X, ArmConstants.MID_NODE_Y).getLowerJoint_degrees());
        assertNotEquals(Double.NaN, new InverseKinematics(ArmConstants.LOW_NODE_X, ArmConstants.LOW_NODE_Y).getLowerJoint_degrees());
    }

    @Disabled
    @Test
    void getUpperJointAngle() {
        assertNotEquals(Double.NaN, new InverseKinematics(ArmConstants.HUMAN_INTAKE_X, ArmConstants.HUMAN_INTAKE_Y).getUpperJoint_degrees());
        assertNotEquals(Double.NaN, new InverseKinematics(ArmConstants.PREPARE_X, ArmConstants.PREPARE_Y).getUpperJoint_degrees());
        assertNotEquals(Double.NaN, new InverseKinematics(ArmConstants.STORAGE_X, ArmConstants.STORAGE_Y).getUpperJoint_degrees());
        assertNotEquals(Double.NaN, new InverseKinematics(ArmConstants.HIGH_NODE_X, ArmConstants.HIGH_NODE_Y).getUpperJoint_degrees());
        assertNotEquals(Double.NaN, new InverseKinematics(ArmConstants.MID_NODE_X, ArmConstants.MID_NODE_Y).getUpperJoint_degrees());
        assertNotEquals(Double.NaN, new InverseKinematics(ArmConstants.LOW_NODE_X, ArmConstants.LOW_NODE_Y).getUpperJoint_degrees());
    }

    @Test
    void zeroAngle() {
        // 0,0 angle is straight out away from the robot
        //
        //
        //   - - - <- arm laying flat
        // ========== <- robot base

        assertEquals(0, new InverseKinematics(ArmConstants.LOWER_JOINT_LENGTH + ArmConstants.UPPER_JOINT_LENGTH, 0).getLowerJoint_degrees());
        assertEquals(0, new InverseKinematics(ArmConstants.LOWER_JOINT_LENGTH + ArmConstants.UPPER_JOINT_LENGTH, 0).getUpperJoint_degrees());
    }

    @Test
    void straightUp() {
        // if the arm is straight up, the lower joint is -90º and the upper joint is 0º
        //   -
        //   -
        //   -
        // ========== <- robot base
        assertEquals(90, new InverseKinematics(0, ArmConstants.LOWER_JOINT_LENGTH + ArmConstants.UPPER_JOINT_LENGTH).getLowerJoint_degrees());
        assertEquals(0, new InverseKinematics(0, ArmConstants.LOWER_JOINT_LENGTH + ArmConstants.UPPER_JOINT_LENGTH).getUpperJoint_degrees());
    }

    @Test
    void straightOut() {
        // if the arm is straight out at a 45º angle, the lower joint is -45º and the upper joint is 0º
        //       -
        //     -
        //   -
        // ========== <- robot base
        var totalArmLength = ArmConstants.LOWER_JOINT_LENGTH + ArmConstants.UPPER_JOINT_LENGTH;
        assertEquals(45, new InverseKinematics(totalArmLength / Math.sqrt(2), totalArmLength / Math.sqrt(2)).getLowerJoint_degrees(), 0.01);
        assertEquals(0, new InverseKinematics(totalArmLength / Math.sqrt(2), totalArmLength / Math.sqrt(2)).getUpperJoint_degrees(), .01);
    }

    @Test
    void scoreLow(){
        assertNotEquals(Double.NaN, new InverseKinematics(ArmConstants.LOW_NODE_X, ArmConstants.LOW_NODE_Y).getLowerJoint_degrees()); //around 110.749 degrees
        assertNotEquals(Double.NaN, new InverseKinematics(ArmConstants.LOW_NODE_X, ArmConstants.LOW_NODE_Y).getUpperJoint_degrees()); //around -129.664 degrees
    }

    @Test
    void scoreMid(){
        assertNotEquals(Double.NaN, new InverseKinematics(ArmConstants.MID_NODE_X, ArmConstants.MID_NODE_Y).getLowerJoint_degrees()); //around 76.984 degrees
        assertNotEquals(Double.NaN, new InverseKinematics(ArmConstants.MID_NODE_X, ArmConstants.MID_NODE_Y).getUpperJoint_degrees()); //around -63.436 degrees
    }

    @Test
    void scoreHigh(){
        assertNotEquals(Double.NaN, new InverseKinematics(ArmConstants.HIGH_NODE_X, ArmConstants.HIGH_NODE_Y).getLowerJoint_degrees()); //around 76.984 degrees
        assertNotEquals(Double.NaN, new InverseKinematics(ArmConstants.HIGH_NODE_X, ArmConstants.HIGH_NODE_Y).getUpperJoint_degrees()); //around -63.436 degrees
    }

    @Test
    void IntakeGround(){
        assertNotEquals(Double.NaN, new InverseKinematics(ArmConstants.INTAKE_GROUND_X, ArmConstants.INTAKE_GROUND_Y).getLowerJoint_degrees()); //around 132.943 degrees
        assertNotEquals(Double.NaN, new InverseKinematics(ArmConstants.INTAKE_GROUND_X, ArmConstants.INTAKE_GROUND_Y).getUpperJoint_degrees()); //around -167.82 degrees
    }

    @Test
    void HumanIntake(){
        assertNotEquals(Double.NaN, new InverseKinematics(ArmConstants.HUMAN_INTAKE_X, ArmConstants.HUMAN_INTAKE_Y).getLowerJoint_degrees()); //around 103.571 degrees
        assertNotEquals(Double.NaN, new InverseKinematics(ArmConstants.HUMAN_INTAKE_X, ArmConstants.HUMAN_INTAKE_Y).getUpperJoint_degrees()); //around -115.770 degrees
    }

    @Test
    void Prepare(){
        assertNotEquals(Double.NaN, new InverseKinematics(ArmConstants.PREPARE_X, ArmConstants.PREPARE_Y).getLowerJoint_degrees()); //around 144.162 degrees
        assertEquals(Double.NaN, new InverseKinematics(ArmConstants.PREPARE_X, ArmConstants.PREPARE_Y).getUpperJoint_degrees()); //around -125.574 degrees
    }

    @Test
    void Store(){
        assertNotEquals(Double.NaN, new InverseKinematics(ArmConstants.STORAGE_X, ArmConstants.STORAGE_Y).getLowerJoint_degrees()); //around 59.577 degrees
        assertNotEquals(Double.NaN, new InverseKinematics(ArmConstants.STORAGE_X, ArmConstants.STORAGE_Y).getUpperJoint_degrees()); //around -152.504 degrees
    }

}