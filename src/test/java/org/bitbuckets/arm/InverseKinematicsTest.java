package org.bitbuckets.arm;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InverseKinematicsTest {

    //@Disabled
    @Test
    void getLowerJointAngle() {
        assertEquals(0.785, new InverseKinematics(1.535 * Math.sin(Math.PI/4), 1.535 * Math.sin(Math.PI/4)).getLowerJointAngle());
    }

    //@Disabled
    @Test
    void getUpperJointAngle() {
        assertEquals(0, new InverseKinematics(1.535 * Math.sin(Math.PI/4), 1.535 * Math.sin(Math.PI/4)).getUpperJointAngle());
    }
}