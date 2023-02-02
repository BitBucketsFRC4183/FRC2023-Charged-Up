package org.bitbuckets.arm;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ForwardKinematicsTest {

    @Disabled
    @Test
    public void getX() {
        assertEquals(0, new ForwardKinematics(45, 0).getX());
    }

    @Disabled
    @Test
    public void getY() {
        assertEquals(0, new ForwardKinematics(45, 0).getY());
    }
}