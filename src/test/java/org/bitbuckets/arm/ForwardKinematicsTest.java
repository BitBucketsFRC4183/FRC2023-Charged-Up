package org.bitbuckets.arm;

import org.bitbuckets.arm.kinematics.ForwardKinematics;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ForwardKinematicsTest {

    @Disabled
    @Test
    public void testX() {
        assertEquals((ArmConstants.LOWER_JOINT_LENGTH + ArmConstants.UPPER_JOINT_LENGTH), new ForwardKinematics(Math.PI/4, 0).getX(), 0.001);
    }

    //@Disabled
    @Test
    public void testY() {
        assertEquals(0, new ForwardKinematics(0, 0).getY(), 0.001);
    }
}