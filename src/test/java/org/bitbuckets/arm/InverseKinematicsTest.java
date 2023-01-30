package org.bitbuckets.arm;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InverseKinematicsTest {

    @Test
    void getLowerJointAngle() {
        assertEquals(0, new InverseKinematics(2. * Math.sqrt(2.), 2. * Math.sqrt(2.)).getLowerJointAngle());
    }

    @Test
    void getUpperJointAngle() {
        assertEquals(0, new InverseKinematics(2. * Math.sqrt(2.), 2. * Math.sqrt(2.)).getUpperJointAngle());
    }
}