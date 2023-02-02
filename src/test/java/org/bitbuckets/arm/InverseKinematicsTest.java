package org.bitbuckets.arm;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InverseKinematicsTest {

    @Disabled
    @Test
    void getLowerJointAngle() {
        assertEquals(0, new InverseKinematics(2. * Math.sqrt(2.), 2. * Math.sqrt(2.)).getLowerJointAngle());
    }

    @Disabled
    @Test
    void getUpperJointAngle() {
        assertEquals(0, new InverseKinematics(2. * Math.sqrt(2.), 2. * Math.sqrt(2.)).getUpperJointAngle());
    }
}