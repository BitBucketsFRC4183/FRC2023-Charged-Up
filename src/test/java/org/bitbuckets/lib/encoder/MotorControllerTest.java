package org.bitbuckets.lib.encoder;

import org.bitbuckets.lib.hardware.IMotorController;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test default methods of IMotorController
 */
class MotorControllerTest {

    double steerReduction = (15.0 / 32.0) * (10.0 / 60.0);
    double steerCoefficient = 2.0 * Math.PI / 2048 * steerReduction;

    @Disabled
    @Test
    void zeroShouldReturnZero() {
        IMotorController motorController = Mockito.mock(IMotorController.class);
        Mockito.when(motorController.getPositionRaw()).thenReturn(0.0);
        Mockito.when(motorController.getVelocityRaw()).thenReturn(0.0);
        Mockito.when(motorController.getTimeFactor()).thenReturn(1.0);
        Mockito.when(motorController.getRawToRotationsFactor()).thenReturn((1.0 / 2048.0));
        Mockito.when(motorController.getMechanismFactor()).thenReturn(1.0);

        assertEquals(Math.toRadians(0), motorController.getEncoderPositionBound_rot());


    }

    @Disabled
    @Test
    void getMechanismPositionAccumulated_radians() {
        IMotorController motorController = Mockito.mock(IMotorController.class);
        Mockito.when(motorController.getPositionRaw()).thenReturn(Math.toRadians(   540) / steerCoefficient);
        Mockito.when(motorController.getVelocityRaw()).thenReturn(0.0);
        Mockito.when(motorController.getTimeFactor()).thenReturn(1.0);
        Mockito.when(motorController.getRawToRotationsFactor()).thenReturn((1.0 / 2048.0));
        Mockito.when(motorController.getMechanismFactor()).thenReturn(steerCoefficient);

        assertEquals(Math.toRadians(540), motorController.getPositionMechanism_meters());
    }

    @Disabled
    @Test
    void getMechanismPositionBounded_radians() {
        IMotorController motorController = Mockito.mock(IMotorController.class);
        Mockito.when(motorController.getPositionRaw()).thenReturn(Math.toRadians(   540) / steerCoefficient);
        Mockito.when(motorController.getVelocityRaw()).thenReturn(0.0);
        Mockito.when(motorController.getTimeFactor()).thenReturn(1.0);
        Mockito.when(motorController.getRawToRotationsFactor()).thenReturn((1.0 / 2048.0));
        Mockito.when(motorController.getMechanismFactor()).thenReturn(steerCoefficient);

        assertEquals(Math.toRadians(180), motorController.getPositionMechanism_meters());
    }

    @Disabled
    @Test
    void getMechanismPositionAccumulated_shouldWorkWithRatio() {
        IMotorController motorController = Mockito.mock(IMotorController.class);
        Mockito.when(motorController.getPositionRaw()).thenReturn(2048.0);
        Mockito.when(motorController.getVelocityRaw()).thenReturn(0.0);
        Mockito.when(motorController.getTimeFactor()).thenReturn(1.0);
        Mockito.when(motorController.getRawToRotationsFactor()).thenReturn((1.0 / 2048.0));
        Mockito.when(motorController.getMechanismFactor()).thenReturn(0.5);

        assertEquals(Math.PI, motorController.getPositionMechanism_meters());
    }
}