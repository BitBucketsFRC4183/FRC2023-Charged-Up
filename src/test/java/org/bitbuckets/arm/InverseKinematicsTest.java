package org.bitbuckets.arm;

import org.junit.jupiter.api.Test;

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
}