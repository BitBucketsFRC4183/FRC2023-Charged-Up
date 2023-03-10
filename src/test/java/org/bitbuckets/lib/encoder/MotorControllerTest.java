package org.bitbuckets.lib.encoder;

import org.bitbuckets.lib.hardware.IMotorController;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test default methods of IMotorController
 */
class MotorControllerTest {

    double steerReduction = (15.0 / 32.0) * (10.0 / 60.0);
    double steerCoefficient = 2048 * steerReduction;

    @Test
    void zeroShouldReturnZero() {
        IMotorController motorController = Mockito.mock(IMotorController.class);
        Mockito.when(motorController.getTimeFactor()).thenReturn(1.0);
        Mockito.when(motorController.getRawToRotationsFactor()).thenReturn((1.0 / 2048.0));
        Mockito.when(motorController.getMechanismFactor()).thenReturn(1.0);

        assertEquals(Math.toRadians(0), motorController.getEncoderPositionBound_rot());


    }

}