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

        assertEquals(0, new InverseKinematics(ArmConstants.LOWER_JOINT_LENGTH + ArmConstants.UPPER_JOINT_LENGTH, 0).getLowerJoint_degrees(), 0.001);
        assertEquals(-0, new InverseKinematics(ArmConstants.LOWER_JOINT_LENGTH + ArmConstants.UPPER_JOINT_LENGTH, 0).getUpperJoint_degrees(), 0.001);
    }

    @Test
    void straightUp() {
        // if the arm is straight up, the lower joint is -90º and the upper joint is 0º
        //   -
        //   -
        //   -
        // ========== <- robot base
        assertEquals(90, new InverseKinematics(0, ArmConstants.LOWER_JOINT_LENGTH + ArmConstants.UPPER_JOINT_LENGTH).getLowerJoint_degrees(), 0.001);
        assertEquals(-0, new InverseKinematics(0, ArmConstants.LOWER_JOINT_LENGTH + ArmConstants.UPPER_JOINT_LENGTH).getUpperJoint_degrees(), 0.001);
    }

    @Test
    void straightOut() {
        // if the arm is straight out at a 45º angle, the lower joint is -45º and the upper joint is 0º
        //       -
        //     -
        //   -
        // ========== <- robot base
        var totalArmLength = ArmConstants.LOWER_JOINT_LENGTH + ArmConstants.UPPER_JOINT_LENGTH;
        assertEquals(45, new InverseKinematics(totalArmLength / Math.sqrt(2), totalArmLength / Math.sqrt(2)).getLowerJoint_degrees(), 0.001);
        assertEquals(-0, new InverseKinematics(totalArmLength / Math.sqrt(2), totalArmLength / Math.sqrt(2)).getUpperJoint_degrees(), 0.001);
    }

    @Test
    void scoreLow(){
        assertEquals(146.621, new InverseKinematics(ArmConstants.LOW_NODE_X, ArmConstants.LOW_NODE_Y).getLowerJoint_degrees(), 0.001); //around 146.621 degrees
        assertEquals(-144.747, new InverseKinematics(ArmConstants.LOW_NODE_X, ArmConstants.LOW_NODE_Y).getUpperJoint_degrees(), 0.001); //around -144.747 degrees
    }

    @Test
    void scoreMid(){
        assertEquals(102.825, new InverseKinematics(ArmConstants.MID_NODE_X, ArmConstants.MID_NODE_Y).getLowerJoint_degrees(), 0.001); //around 102.825 degrees
        assertEquals(-94.034, new InverseKinematics(ArmConstants.MID_NODE_X, ArmConstants.MID_NODE_Y).getUpperJoint_degrees(), 0.001); //around -94.034 degrees
    }

    @Test
    void scoreHigh(){
        assertEquals(102.825, new InverseKinematics(ArmConstants.HIGH_NODE_X, ArmConstants.HIGH_NODE_Y).getLowerJoint_degrees(), 0.001); //around 102.825 degrees
        assertEquals(-94.034, new InverseKinematics(ArmConstants.HIGH_NODE_X, ArmConstants.HIGH_NODE_Y).getUpperJoint_degrees(), 0.001); //around -94.034 degrees
    }

    @Test
    void IntakeGround(){
        assertEquals(Double.NaN, new InverseKinematics(ArmConstants.INTAKE_GROUND_X, ArmConstants.INTAKE_GROUND_Y).getLowerJoint_degrees()); //around 132.943 degrees
        assertEquals(Double.NaN, new InverseKinematics(ArmConstants.INTAKE_GROUND_X, ArmConstants.INTAKE_GROUND_Y).getUpperJoint_degrees()); //around -167.82 degrees
    }

    @Test
    void HumanIntake(){
        assertEquals(133.348, new InverseKinematics(ArmConstants.HUMAN_INTAKE_X, ArmConstants.HUMAN_INTAKE_Y).getLowerJoint_degrees(), 0.001); //around 133.348 degrees
        assertEquals(-132.585, new InverseKinematics(ArmConstants.HUMAN_INTAKE_X, ArmConstants.HUMAN_INTAKE_Y).getUpperJoint_degrees(), 0.001); //around -132.585 degrees
    }

    @Test
    void Prepare(){
        assertEquals(177.776, new InverseKinematics(ArmConstants.PREPARE_X, ArmConstants.PREPARE_Y).getLowerJoint_degrees(), 0.001); //around 177.776 degrees
        assertEquals(-141.055, new InverseKinematics(ArmConstants.PREPARE_X, ArmConstants.PREPARE_Y).getUpperJoint_degrees(), 0.001); //around -141.055 degrees
    }

    @Test
    void Store(){
        //0.3, -0.1
        assertEquals(133.151, new InverseKinematics(ArmConstants.STORAGE_X, ArmConstants.STORAGE_Y).getLowerJoint_degrees(), 0.001); //around 59.577 degrees
        assertEquals(-170.982, new InverseKinematics(ArmConstants.STORAGE_X, ArmConstants.STORAGE_Y).getUpperJoint_degrees(), 0.001); //around -152.504 degrees
    }


}