package org.bitbuckets.arm;

import org.bitbuckets.arm.kinematics.ForwardKinematics;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.bitbuckets.arm.kinematics.InverseKinematics;


import static org.junit.jupiter.api.Assertions.assertEquals;

class ForwardKinematicsTest {

    //Annoying test
    @Disabled
    @Test
    public void testX() {
        assertEquals((ArmConstants.LOWER_JOINT_LENGTH + ArmConstants.UPPER_JOINT_LENGTH)/Math.sqrt(2), new ForwardKinematics(45.0, 0).getX(), 0.001);
    }

    //Annoying test
    @Disabled
    @Test
    public void testY() {
        assertEquals(0, new ForwardKinematics(0, 0).getY(), 0.001);
    }

    @Test
    public void testInverseWithForward(){
        double theta1 = new InverseKinematics(ArmConstants.MID_NODE_X, ArmConstants.MID_NODE_Y).getLowerJoint_degrees();
        double theta2 = new InverseKinematics(ArmConstants.MID_NODE_X, ArmConstants.MID_NODE_Y).getUpperJoint_degrees();

        assertEquals(ArmConstants.MID_NODE_X, new ForwardKinematics(theta1, theta2).getX(), 0.001);
        assertEquals(ArmConstants.MID_NODE_Y, new ForwardKinematics(theta1, theta2).getY(), 0.001);

    }
}