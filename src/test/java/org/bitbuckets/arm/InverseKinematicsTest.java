package org.bitbuckets.arm;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class InverseKinematicsTest {

    @Test
    void getLowerJointAngle() {
        assertEquals(Double.NaN, new InverseKinematics(ArmConstants.HUMAN_INTAKE_X, ArmConstants.HUMAN_INTAKE_Y).getLowerJointAngle());
        assertEquals(Double.NaN, new InverseKinematics(ArmConstants.PREPARE_X, ArmConstants.PREPARE_Y).getLowerJointAngle());
        assertEquals(Double.NaN, new InverseKinematics(ArmConstants.STORAGE_X, ArmConstants.STORAGE_Y).getLowerJointAngle());
        assertEquals(Double.NaN, new InverseKinematics(ArmConstants.HIGH_NODE_X, ArmConstants.HIGH_NODE_Y).getLowerJointAngle());
        assertEquals(Double.NaN, new InverseKinematics(ArmConstants.MID_NODE_X, ArmConstants.MID_NODE_Y).getLowerJointAngle());
        assertEquals(Double.NaN, new InverseKinematics(ArmConstants.LOW_NODE_X, ArmConstants.LOW_NODE_Y).getLowerJointAngle());



    }

    @Test
    void getUpperJointAngle() {

        assertEquals(Double.NaN, new InverseKinematics(ArmConstants.HUMAN_INTAKE_X, ArmConstants.HUMAN_INTAKE_Y).getUpperJointAngle());
        assertEquals(Double.NaN, new InverseKinematics(ArmConstants.PREPARE_X, ArmConstants.PREPARE_Y).getUpperJointAngle());
        assertEquals(Double.NaN, new InverseKinematics(ArmConstants.STORAGE_X, ArmConstants.STORAGE_Y).getUpperJointAngle());
        assertEquals(Double.NaN, new InverseKinematics(ArmConstants.HIGH_NODE_X, ArmConstants.HIGH_NODE_Y).getUpperJointAngle());
        assertEquals(Double.NaN, new InverseKinematics(ArmConstants.MID_NODE_X, ArmConstants.MID_NODE_Y).getUpperJointAngle());
        assertEquals(Double.NaN, new InverseKinematics(ArmConstants.LOW_NODE_X, ArmConstants.LOW_NODE_Y).getUpperJointAngle());

    }
}