package org.bitbuckets.drive;
/*

import com.ctre.phoenix.motorcontrol.TalonFXControlMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;
import org.bitbuckets.drive.DriveSDSConstants;
import org.bitbuckets.drive.controlsds.falcon.Falcon500DriveController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class Falcon500DriveControllerTest {

    WPI_TalonFX motor;

    @BeforeEach
    void setUp() {
        motor = mock();
    }

    @Test
    void setReferenceVoltage() {
        var controller = new Falcon500DriveController(motor, 1, 12);

        controller.setReferenceVoltage(9);

        // should have called set with 9/12th voltage
        verify(motor).set(eq(TalonFXControlMode.PercentOutput), eq(9.0 / 12));
    }

    @Test
    void getStateVelocity() throws InterruptedException {
        var controller = new Falcon500DriveController(motor, 1, 12);

        when(motor.getSelectedSensorVelocity()).thenReturn(6500d);

        // simulated speed of 6500 ticks/100ms
        assertEquals(6500, controller.getStateVelocity());

        // update velocity coefficient to use the drive reduction, should report double speed
        double sensorPositionCoefficient = Math.PI * DriveSDSConstants.MK4_L2.getWheelDiameter() * DriveSDSConstants.MK4_L2.getDriveReduction() / DriveSDSConstants.TICKS_PER_ROTATION;
        double sensorVelocityCoefficient = sensorPositionCoefficient * 10.0;

        controller = new Falcon500DriveController(motor, sensorVelocityCoefficient, 12);
        assertEquals(1.5, controller.getStateVelocity(), .1);
    }
}*/
