package org.bitbuckets.drive;

import com.revrobotics.RelativeEncoder;
import org.bitbuckets.drive.controlsds.neo.NeoDriveController;
import org.bitbuckets.lib.hardware.IMotorController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class NeoDriveControllerTest {

    IMotorController motor;

    RelativeEncoder encoder;

    @BeforeEach
    void setUp() {
        motor = mock();
        encoder = mock();
    }

    @Test
    void setReferenceVoltage() {
        var controller = new NeoDriveController(motor, 12);

        controller.setReferenceVoltage(9);

        // should have called set with 9/12th voltage
        verify(motor).moveAtPercent(eq(9.0 / 12));
    }

    /*
    @Test
    void getStateVelocity() throws InterruptedException {
        var controller = new NeoDriveController(motor,  12);

        when(motor.getEncoder()).thenReturn(encoder);

        //6500 tics per 100 ms == 31.7
        when(encoder.getVelocity()).thenReturn(31.7);

        // simulated speed of 6500 ticks/100ms
        assertEquals(31.7, controller.getStateVelocity());

        // update velocity coefficient to use the drive reduction, should report double speed
        double sensorPositionCoefficient = Math.PI * DriveSDSConstants.MK4I_L2.getWheelDiameter() * DriveSDSConstants.MK4_L2.getDriveReduction();
        double sensorVelocityCoefficient = sensorPositionCoefficient;

        controller = new NeoDriveController(motor, 12);
        assertEquals(1.5, controller.getStateVelocity(), .1);
    }*/
}