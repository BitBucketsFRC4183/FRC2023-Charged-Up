package org.bitbuckets.arm;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InverseKinematicsTest {

    @Disabled
    @Test
    void getLowerJointAngle() {
        assertEquals(Double.NaN, new InverseKinematics(2, 2).getLowerJointAngle());
    }

    @Disabled
    @Test
    void getUpperJointAngle() {
        assertEquals(Double.NaN, new InverseKinematics(2, 2).getUpperJointAngle());
    }
}