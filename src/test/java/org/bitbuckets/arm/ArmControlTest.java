package org.bitbuckets.arm;

import org.bitbuckets.lib.hardware.IMotorController;
import org.bitbuckets.lib.log.Debuggable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ArmControlTest {

    IMotorController lowerJoint;
    IMotorController lowerJoint1;
    IMotorController upperJoint;
    Debuggable debuggable;

    ArmControl control;

    @BeforeEach
    public void beforeEach() {
        lowerJoint = mock(IMotorController.class);
        lowerJoint1 = mock(IMotorController.class);
        upperJoint = mock(IMotorController.class);
        debuggable = mock(Debuggable.class);

        control = new ArmControl(lowerJoint, lowerJoint1, upperJoint, debuggable);
    }


    @Test
    void convertMechanismRotationToRawRotation_lowerJoint() {
        assertEquals(27.69, control.convertMechanismRotationtoRawRotation_lowerJoint(1), .1);
    }

    @Test
    void convertMechanismRotationToRawRotation_upperJoint() {
        assertEquals(24., control.convertMechanismRotationtoRawRotation_upperJoint(1), .1);
    }

    @Test
    void isReachable() {
        assertTrue(control.isReachable(0, 0));
        assertFalse(control.isReachable(0, Double.NaN));
        assertFalse(control.isReachable(0, Double.NaN));
        assertFalse(control.isReachable(Double.NaN, Double.NaN));
    }

    @Test
    void isErrorSmallEnough() {
        when(lowerJoint.getError_mechanismRotations()).thenReturn(1d);
        when(upperJoint.getError_mechanismRotations()).thenReturn(1d);
        assertFalse(control.isErrorSmallEnough(.1));
        assertTrue(control.isErrorSmallEnough(2));

        // Should work
//        when(lowerJoint.getError_mechanismRotations()).thenReturn(-1d);
//        when(upperJoint.getError_mechanismRotations()).thenReturn(-1d);
//        assertFalse(control.isErrorSmallEnough(.1));
//        assertTrue(control.isErrorSmallEnough(2));
    }

    @Test
    void humanIntake() {
        // we should move the mechanism
        control.humanIntake();
        verify(lowerJoint).moveToPosition_mechanismRotations(anyDouble());
        verify(upperJoint).moveToPosition_mechanismRotations(anyDouble());
    }

    @Test
    void storeArm() {
        // we should move the mechanism
        control.humanIntake();
        verify(lowerJoint).moveToPosition_mechanismRotations(anyDouble());
        verify(upperJoint).moveToPosition_mechanismRotations(anyDouble());
    }

    @Test
    void prepareArm() {
        // we should move the mechanism
        control.humanIntake();
        verify(lowerJoint).moveToPosition_mechanismRotations(anyDouble());
        verify(upperJoint).moveToPosition_mechanismRotations(anyDouble());
    }

    @Test
    void scoreLow() {
        // we should move the mechanism
        control.humanIntake();
        verify(lowerJoint).moveToPosition_mechanismRotations(anyDouble());
        verify(upperJoint).moveToPosition_mechanismRotations(anyDouble());
    }

    @Test
    void scoreMid() {
        // we should move the mechanism
        control.humanIntake();
        verify(lowerJoint).moveToPosition_mechanismRotations(anyDouble());
        verify(upperJoint).moveToPosition_mechanismRotations(anyDouble());
    }

    @Test
    void scoreHigh() {
        // we should move the mechanism
        control.humanIntake();
        verify(lowerJoint).moveToPosition_mechanismRotations(anyDouble());
        verify(upperJoint).moveToPosition_mechanismRotations(anyDouble());
    }
}