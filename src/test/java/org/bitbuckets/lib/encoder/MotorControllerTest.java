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


    @Test
    void zeroShouldReturnZero() {
        IMotorController motorController = new TestMotorController(1.0, (1.0/2048.0), 0.0);

        assertEquals(Math.toRadians(0), motorController.getMechanismPositionAccumulated_radians());
    }


    @Test
    void getMechanismPositionAccumulated_radians() {
        IMotorController motorController = new TestMotorController(steerCoefficient, (1.0 / 2048.0), Math.toRadians(540) / steerCoefficient);

        assertEquals(Math.toRadians(540), motorController.getMechanismPositionAccumulated_radians());
    }


    @Test
    void getMechanismPositionBounded_radians() {
        IMotorController motorController = new TestMotorController(steerCoefficient, (1.0 / 2048.0), Math.toRadians(540) / steerCoefficient);

        assertEquals(Math.toRadians(180), motorController.getMechanismPositionBounded_radians());
    }

    @Test
    void getMechanismPositionAccumulated_shouldWorkWithRatio() {
        IMotorController motorController = new TestMotorController(0.5, (1.0 / 2048.0), 2048.0);

        assertEquals(Math.PI, motorController.getMechanismPositionAccumulated_radians());
    }
}