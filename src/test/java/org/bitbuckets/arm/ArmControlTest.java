package org.bitbuckets.arm;

import org.bitbuckets.arm.kinematics.InverseKinematics;
import org.bitbuckets.lib.hardware.IMotorController;
import org.bitbuckets.lib.debug.IDebuggable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ArmControlTest {

    IMotorController lowerJoint;
    IMotorController lowerJoint1;
    IMotorController upperJoint;
    IDebuggable debuggable;

    ArmControl control;


    @BeforeEach
    public void beforeEach() {
        lowerJoint = mock(IMotorController.class);
        lowerJoint1 = mock(IMotorController.class);
        upperJoint = mock(IMotorController.class);
        debuggable = mock(IDebuggable.class);

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
        control.storeArm();
        verify(lowerJoint).moveToPosition_mechanismRotations(anyDouble());
        verify(upperJoint).moveToPosition_mechanismRotations(anyDouble());
    }

    @Test
    void prepareArm() {
        // we should move the mechanism
        control.prepareArm();
        verify(lowerJoint).moveToPosition_mechanismRotations(anyDouble());
        verify(upperJoint).moveToPosition_mechanismRotations(anyDouble());
    }

    @Test
    void scoreLow() {
        // we should move the mechanism
        control.scoreLow();
        verify(lowerJoint).moveToPosition_mechanismRotations(anyDouble());
        verify(upperJoint).moveToPosition_mechanismRotations(anyDouble());
    }

    @Test
    void scoreMid() {
        // we should move the mechanism
        control.scoreMid();
        verify(lowerJoint).moveToPosition_mechanismRotations(anyDouble());
        verify(upperJoint).moveToPosition_mechanismRotations(anyDouble());
    }

    @Test
    void scoreHigh() {
        // we should move the mechanism
        control.scoreHigh();
        verify(lowerJoint).moveToPosition_mechanismRotations(anyDouble());
        verify(upperJoint).moveToPosition_mechanismRotations(anyDouble());
    }


    @Test
    void isReachable() {

        double lowerDegrees = new InverseKinematics(.5, .5).getLowerJoint_degrees();
        double upperDegrees = new InverseKinematics(.5, .5).getUpperJoint_degrees();

        assertTrue(control.isReachable(lowerDegrees, upperDegrees));
        //assertFalse(control.isReachable(0, Double.NaN));
        //assertFalse(control.isReachable(0, Double.NaN));
        //assertFalse(control.isReachable(Double.NaN, Double.NaN));


    }

}