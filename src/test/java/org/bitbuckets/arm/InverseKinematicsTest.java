package org.bitbuckets.arm;

import config.Arm;
import org.bitbuckets.arm.kinematics.InverseKinematics;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class InverseKinematicsTest {

    @Test
    void getLowerJointAngle() {
        assertNotEquals(Double.NaN, new InverseKinematics(Arm.HUMAN_INTAKE_X, Arm.HUMAN_INTAKE_Y).getLowerJoint_degrees());
        assertNotEquals(Double.NaN, new InverseKinematics(Arm.PREPARE_X, Arm.PREPARE_Y).getLowerJoint_degrees());
        assertNotEquals(Double.NaN, new InverseKinematics(Arm.STORAGE_X, Arm.STORAGE_Y).getLowerJoint_degrees());
        assertNotEquals(Double.NaN, new InverseKinematics(Arm.HIGH_NODE_X, Arm.HIGH_NODE_Y).getLowerJoint_degrees());
        assertNotEquals(Double.NaN, new InverseKinematics(Arm.MID_NODE_X, Arm.MID_NODE_Y).getLowerJoint_degrees());
        assertNotEquals(Double.NaN, new InverseKinematics(Arm.LOW_NODE_X, Arm.LOW_NODE_Y).getLowerJoint_degrees());
    }

    @Test
    void getUpperJointAngle() {
        assertNotEquals(Double.NaN, new InverseKinematics(Arm.HUMAN_INTAKE_X, Arm.HUMAN_INTAKE_Y).getUpperJoint_degrees());
        assertNotEquals(Double.NaN, new InverseKinematics(Arm.PREPARE_X, Arm.PREPARE_Y).getUpperJoint_degrees());
        assertNotEquals(Double.NaN, new InverseKinematics(Arm.STORAGE_X, Arm.STORAGE_Y).getUpperJoint_degrees());
        assertNotEquals(Double.NaN, new InverseKinematics(Arm.HIGH_NODE_X, Arm.HIGH_NODE_Y).getUpperJoint_degrees());
        assertNotEquals(Double.NaN, new InverseKinematics(Arm.MID_NODE_X, Arm.MID_NODE_Y).getUpperJoint_degrees());
        assertNotEquals(Double.NaN, new InverseKinematics(Arm.LOW_NODE_X, Arm.LOW_NODE_Y).getUpperJoint_degrees());
    }

    @Test
    void zeroAngle() {
        // 0,0 angle is straight out away from the robot
        //
        //
        //   - - - <- arm laying flat
        // ========== <- robot base

        assertEquals(0, new InverseKinematics(Arm.LOWER_JOINT_LENGTH + Arm.UPPER_JOINT_LENGTH, 0).getLowerJoint_degrees());
        assertEquals(0, new InverseKinematics(Arm.LOWER_JOINT_LENGTH + Arm.UPPER_JOINT_LENGTH, 0).getUpperJoint_degrees());
    }

    @Test
    void straightUp() {
        // if the arm is straight up, the lower joint is -90º and the upper joint is 0º
        //   -
        //   -
        //   -
        // ========== <- robot base
        assertEquals(90, new InverseKinematics(0, Arm.LOWER_JOINT_LENGTH + Arm.UPPER_JOINT_LENGTH).getLowerJoint_degrees());
        assertEquals(0, new InverseKinematics(0, Arm.LOWER_JOINT_LENGTH + Arm.UPPER_JOINT_LENGTH).getUpperJoint_degrees());
    }

    @Test
    void straightOut() {
        // if the arm is straight out at a 45º angle, the lower joint is -45º and the upper joint is 0º
        //       -
        //     -
        //   -
        // ========== <- robot base
        var totalArmLength = Arm.LOWER_JOINT_LENGTH + Arm.UPPER_JOINT_LENGTH;
        assertEquals(45, new InverseKinematics(totalArmLength / Math.sqrt(2), totalArmLength / Math.sqrt(2)).getLowerJoint_degrees(), 0.01);
        assertEquals(0, new InverseKinematics(totalArmLength / Math.sqrt(2), totalArmLength / Math.sqrt(2)).getUpperJoint_degrees(), .01);
    }

    @Test
    void scoreLow(){
        assertNotEquals(Double.NaN, new InverseKinematics(Arm.LOW_NODE_X, Arm.LOW_NODE_Y).getLowerJoint_degrees()); //around 110.749 degrees
        assertNotEquals(Double.NaN, new InverseKinematics(Arm.LOW_NODE_X, Arm.LOW_NODE_Y).getUpperJoint_degrees()); //around -129.664 degrees
    }

    @Test
    void scoreMid(){
        assertNotEquals(Double.NaN, new InverseKinematics(Arm.MID_NODE_X, Arm.MID_NODE_Y).getLowerJoint_degrees()); //around 76.984 degrees
        assertNotEquals(Double.NaN, new InverseKinematics(Arm.MID_NODE_X, Arm.MID_NODE_Y).getUpperJoint_degrees()); //around -63.436 degrees
    }

    @Test
    void scoreHigh(){
        assertNotEquals(Double.NaN, new InverseKinematics(Arm.HIGH_NODE_X, Arm.HIGH_NODE_Y).getLowerJoint_degrees()); //around 76.984 degrees
        assertNotEquals(Double.NaN, new InverseKinematics(Arm.HIGH_NODE_X, Arm.HIGH_NODE_Y).getUpperJoint_degrees()); //around -63.436 degrees
    }

    @Test
    void IntakeGround(){
        assertNotEquals(Double.NaN, new InverseKinematics(Arm.INTAKE_GROUND_X, Arm.INTAKE_GROUND_Y).getLowerJoint_degrees()); //around 132.943 degrees
        assertNotEquals(Double.NaN, new InverseKinematics(Arm.INTAKE_GROUND_X, Arm.INTAKE_GROUND_Y).getUpperJoint_degrees()); //around -167.82 degrees
    }

    @Test
    void HumanIntake(){
        assertNotEquals(Double.NaN, new InverseKinematics(Arm.HUMAN_INTAKE_X, Arm.HUMAN_INTAKE_Y).getLowerJoint_degrees()); //around 103.571 degrees
        assertNotEquals(Double.NaN, new InverseKinematics(Arm.HUMAN_INTAKE_X, Arm.HUMAN_INTAKE_Y).getUpperJoint_degrees()); //around -115.770 degrees
    }

    @Test
    void Prepare(){
        assertNotEquals(Double.NaN, new InverseKinematics(Arm.PREPARE_X, Arm.PREPARE_Y).getLowerJoint_degrees()); //around 144.162 degrees
        assertEquals(Double.NaN, new InverseKinematics(Arm.PREPARE_X, Arm.PREPARE_Y).getUpperJoint_degrees()); //around -125.574 degrees
    }

    @Test
    void Store(){
        assertNotEquals(Double.NaN, new InverseKinematics(Arm.STORAGE_X, Arm.STORAGE_Y).getLowerJoint_degrees()); //around 59.577 degrees
        assertNotEquals(Double.NaN, new InverseKinematics(Arm.STORAGE_X, Arm.STORAGE_Y).getUpperJoint_degrees()); //around -152.504 degrees
    }

}