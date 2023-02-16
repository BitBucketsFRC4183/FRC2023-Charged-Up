package org.bitbuckets.arm;

import org.bitbuckets.lib.log.Debuggable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ArmSubsystemTest {

    ArmInput armInput;
    ArmControl armControl;
    Debuggable debuggable;

    ArmSubsystem armSubsystem;

    @BeforeEach
    public void beforeEach() {
        armInput = mock(ArmInput.class);
        armControl = mock(ArmControl.class);
        debuggable = mock(Debuggable.class);

        armSubsystem = new ArmSubsystem(armInput, armControl, debuggable);
    }

    @Disabled
    @Test
    void teleopPeriodicCalibrate() {
        // call it without a button and ensure calibrate wasn't pressed
        armSubsystem.teleopPeriodic();
        verify(armControl, never()).calibrateLowerArm();
        verify(armControl, never()).calibrateUpperArm();

        // should calibrate now
        when(armInput.isCalibratedPressed()).thenReturn(true);
        armSubsystem.teleopPeriodic();
        verify(armControl, atMostOnce()).calibrateLowerArm();
        verify(armControl, atMostOnce()).calibrateUpperArm();
    }

    @Disabled
    @Test
    void teleopManualModeNoMovement() {
        // no input, but default to move at 0
        armSubsystem.teleopPeriodic();
        verify(armControl, times(1)).manuallyMoveLowerArm(eq(0d));
        verify(armControl, times(1)).manuallyMoveUpperArm(eq(0d));
        verifyNoMoreInteractions(armControl);
    }

    @Disabled
    @Test
    void teleopManualMode() {

        // should move manually
        when(armInput.getLowerArm_PercentOutput()).thenReturn(.5);
        when(armInput.getUpperArm_PercentOutput()).thenReturn(-.5);
        armSubsystem.teleopPeriodic();
        verify(armControl, times(1)).manuallyMoveLowerArm(.5);
        verify(armControl, times(1)).manuallyMoveUpperArm(-.5);
        verifyNoMoreInteractions(armControl);
    }

    @Disabled
    @Test
    void teleopPeriodicScoreHigh() {
        // the operator pressed score high
        when(armInput.isScoreHighPressed()).thenReturn(true);
        armSubsystem.teleopPeriodic();

        // should switch states
        assertEquals(ArmFSM.PREPARE, armSubsystem.state);
        assertEquals(ArmFSM.SCORE_HIGH, armSubsystem.nextState);

        // we are in prepare, but we aren't ready yet
        when(armControl.isErrorSmallEnough(anyDouble())).thenReturn(false);
        armSubsystem.teleopPeriodic();

        // should NOT switch states yet but we do call prepare
        assertEquals(ArmFSM.PREPARE, armSubsystem.state);
        assertEquals(ArmFSM.SCORE_HIGH, armSubsystem.nextState);
        verify(armControl, times(1)).prepareArm();

        // we have finished our prepare state, we should call prepare again and then switch
        when(armControl.isErrorSmallEnough(anyDouble())).thenReturn(true);
        armSubsystem.teleopPeriodic();
        verify(armControl, times(2)).prepareArm();
        assertEquals(ArmFSM.SCORE_HIGH, armSubsystem.state);
        assertEquals(ArmFSM.SCORE_HIGH, armSubsystem.nextState);

        // last call should call scoreHigh
        armSubsystem.teleopPeriodic();
        verify(armControl, times(1)).scoreHigh();
    }
}