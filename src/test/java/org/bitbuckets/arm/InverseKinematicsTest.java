package org.bitbuckets.arm;

import org.bitbuckets.arm.kinematics.InverseKinematics;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class InverseKinematicsTest {

    @Test
    void getLowerJointAngle() {
        //13.849 for lowerJoint
        assertNotEquals(Double.NaN, new InverseKinematics(ArmConstants.HUMAN_INTAKE_X, ArmConstants.HUMAN_INTAKE_Y).getLowerJoint_degrees());
        assertNotEquals(Double.NaN, new InverseKinematics(ArmConstants.PREPARE_X, ArmConstants.PREPARE_Y).getLowerJoint_degrees());
        assertNotEquals(Double.NaN, new InverseKinematics(ArmConstants.STORAGE_X, ArmConstants.STORAGE_Y).getLowerJoint_degrees());
        assertNotEquals(Double.NaN, new InverseKinematics(ArmConstants.HIGH_NODE_X, ArmConstants.HIGH_NODE_Y).getLowerJoint_degrees());
        assertNotEquals(Double.NaN, new InverseKinematics(ArmConstants.MID_NODE_X, ArmConstants.MID_NODE_Y).getLowerJoint_degrees());
        assertNotEquals(Double.NaN, new InverseKinematics(ArmConstants.LOW_NODE_X, ArmConstants.LOW_NODE_Y).getLowerJoint_degrees());
    }

    @Test
    void getUpperJointAngle() {
        //115.96699229928251 for upperJoint
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
        assertEquals(-90, new InverseKinematics(0, ArmConstants.LOWER_JOINT_LENGTH + ArmConstants.UPPER_JOINT_LENGTH).getLowerJoint_degrees());
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
        assertEquals(-45, new InverseKinematics(totalArmLength / Math.sqrt(2), totalArmLength / Math.sqrt(2)).getLowerJoint_degrees());
        assertEquals(0, new InverseKinematics(totalArmLength / Math.sqrt(2), totalArmLength / Math.sqrt(2)).getUpperJoint_degrees(), .01);
    }

}