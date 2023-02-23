package org.bitbuckets.arm;

import org.bitbuckets.arm.kinematics.ForwardKinematics;
import org.bitbuckets.arm.kinematics.InverseKinematics;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ForwardKinematicsTest {

    //@Disabled
    @Test
    public void testStraightRightX() {
        assertEquals((ArmConstants.LOWER_JOINT_LENGTH + ArmConstants.UPPER_JOINT_LENGTH), new ForwardKinematics(0, 0).getX(), 0.01);
    }
    //@Disabled
    @Test
    public void testStraightRightY() {
        assertEquals(0, new ForwardKinematics(0, 0).getY(), 0.01);
    }

    @Test
    public void testStraight45DegreesX(){
        assertEquals((ArmConstants.LOWER_JOINT_LENGTH + ArmConstants.UPPER_JOINT_LENGTH)/Math.sqrt(2), new ForwardKinematics(Math.PI/4, 0).getX(), 0.01);
    }
    @Test
    public void testStraight45DegreesY(){
        assertEquals((ArmConstants.LOWER_JOINT_LENGTH + ArmConstants.UPPER_JOINT_LENGTH)/Math.sqrt(2), new ForwardKinematics(Math.PI/4, 0).getY(), 0.01);
    }

    /**
     * Should
     */
    InverseKinematics scoreLow = new InverseKinematics(ArmConstants.LOW_NODE_X, ArmConstants.LOW_NODE_Y);
    double lowerAngle = scoreLow.getLowerJoint_degrees();
    double upperAngle = scoreLow.getUpperJoint_degrees();

    @Test
    public void testScoreLowX(){
        assertEquals(ArmConstants.LOW_NODE_X, new ForwardKinematics(lowerAngle, upperAngle).getX(), 0.001);
    }
    @Test
    public void testScoreLowY(){
        assertEquals(ArmConstants.LOW_NODE_Y, new ForwardKinematics(lowerAngle, upperAngle).getY(), 0.001);
    }

}