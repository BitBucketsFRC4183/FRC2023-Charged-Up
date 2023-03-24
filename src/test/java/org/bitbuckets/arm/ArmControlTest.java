package org.bitbuckets.arm;

import config.Arm;
import org.bitbuckets.lib.control.IPIDCalculator;
import org.bitbuckets.lib.hardware.IAbsoluteEncoder;
import org.bitbuckets.lib.hardware.IMotorController;
import org.bitbuckets.lib.log.IDebuggable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.AdditionalMatchers;

import static org.mockito.Mockito.*;

class ArmControlTest {

    ArmDynamics ff;
    IMotorController lowerArm;
    IMotorController upperArm;
    IPIDCalculator lowerArmControl;
    IPIDCalculator upperArmControl;
    IMotorController gripperActuator;
    IMotorController gripperClawMotor;
    IAbsoluteEncoder clawAbsEncoder;
    IDebuggable debuggable;
    ArmControl control;

    @BeforeEach
    void beforeEach() {
        ff = mock(ArmDynamics.class);
        lowerArm = mock(IMotorController.class);
        upperArm = mock(IMotorController.class);
        lowerArmControl = mock(IPIDCalculator.class);
        upperArmControl = mock(IPIDCalculator.class);
        gripperActuator = mock(IMotorController.class);
        gripperClawMotor = mock(IMotorController.class);
        clawAbsEncoder = mock(IAbsoluteEncoder.class);
        debuggable = mock(IDebuggable.class);
        control = new ArmControl(
                ff,
                lowerArm,
                upperArm,
                lowerArmControl,
                upperArmControl,
                gripperActuator,
                gripperClawMotor,
                clawAbsEncoder,
                debuggable
        );
    }

    @Test
    void zeroArmAbs() {
        // when the abs encoder returns .1 after offset, we should zero to .1
        when(upperArm.getAbsoluteEncoder_rotations()).thenReturn(.1 + Arm.UPPER_ARM_OFFSET);
        control.zeroArmAbs();
        verify(upperArm).forceOffset_mechanismRotations(AdditionalMatchers.eq(.1, .01));

        // when the abs encoder returns .9 after offset, we should zero to -.1
        when(upperArm.getAbsoluteEncoder_rotations()).thenReturn(.9 + Arm.UPPER_ARM_OFFSET);
        control.zeroArmAbs();
        verify(upperArm).forceOffset_mechanismRotations(AdditionalMatchers.eq(-.1, .01));
    }

    @Test
    void zeroClawAbs() {

        // when the abs encoder returns .1 after offset, we should zero to .1
        when(clawAbsEncoder.getAbsoluteAngle()).thenReturn(.1);
        control.zeroClawAbs();
        verify(gripperClawMotor).forceOffset_mechanismRotations(AdditionalMatchers.eq(.1, .01));

        // when the abs encoder returns .9 after offset, we should zero to -.1
        when(clawAbsEncoder.getAbsoluteAngle()).thenReturn(.9);
        control.zeroClawAbs();
        verify(gripperClawMotor).forceOffset_mechanismRotations(AdditionalMatchers.eq(-.1, .01));
    }
}