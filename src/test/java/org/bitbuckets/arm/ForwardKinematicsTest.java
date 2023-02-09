package org.bitbuckets.arm;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ForwardKinematicsTest {

    //@Disabled
    @Test
    public void getX() {
        assertEquals(1.535 * Math.sqrt(2) / 2, new ForwardKinematics(Math.PI/4, 0).getX(), 0.01);
    }

    //@Disabled
    @Test
    public void getY() {
        assertEquals(1.535 * Math.sqrt(2) / 2, new ForwardKinematics(Math.PI/4, 0).getY(), 0.01);
    }
}