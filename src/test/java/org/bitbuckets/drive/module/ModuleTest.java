package org.bitbuckets.drive.module;

import org.bitbuckets.drive.old.FilteredEncoder;
import org.bitbuckets.lib.hardware.IEncoder;
import org.bitbuckets.lib.hardware.IMotor;
import org.bitbuckets.lib.log.DataLogger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class ModuleTest {

    @Mock
    IMotor drive;

    @Mock
    IMotor turn;

    @Mock
    IEncoder driveEncoder;

    @Mock
    IEncoder turnEncoder;

    @Mock
    DataLogger<ModuleDataAutoGen> logger;

    FilteredEncoder filteredEncoder;

    @BeforeEach
    public void beforeEach() {
        // Using a regular IEncoder to let the filtered encoder handle the math of converting
        // raw encoder angles to absolute angles
        filteredEncoder = new FilteredEncoder(turnEncoder);
        when(turnEncoder.getMechanismFactor()).thenReturn(1d);
    }

    @Test
    void commandSetpointValuesNone() {
        var module = new DriveModule(drive, turn, driveEncoder, filteredEncoder, logger);

        // encoder is at 0, module told to go to 0 m/s, 0 rad/s
        when(turnEncoder.getEncoderPositionAccumulated_radians()).thenReturn(0d);
        module.commandSetpointValues(0, 0);

        // should do nothing
        Mockito.verify(drive, Mockito.times(1)).moveAtPercent(0);
        Mockito.verify(turn, Mockito.times(1)).moveToPosition(0);
    }

    @Test
    void commandSetpointValuesForward() {
        var module = new DriveModule(drive, turn, driveEncoder, filteredEncoder, logger);

        // encoder is at 0, module told to go to 1 m/s, 0 rads
        when(turnEncoder.getEncoderPositionAccumulated_radians()).thenReturn(0d);
        module.commandSetpointValues(1, 0);

        // should drive forward, not turn
        Mockito.verify(drive, Mockito.times(1)).moveAtPercent(1);
        Mockito.verify(turn, Mockito.times(1)).moveToPosition(0);
    }

    @Test
    void commandSetpointValuesTurn() {
        var module = new DriveModule(drive, turn, driveEncoder, filteredEncoder, logger);

        // encoder is at 0, module told to go to 0 m/s, 90 degrees
        when(turnEncoder.getEncoderPositionAccumulated_radians()).thenReturn(Math.toRadians(90));
        module.commandSetpointValues(0, Math.toRadians(90));

        // should not drive, but should rotate the motor 90 degrees in sensor units
        Mockito.verify(drive, Mockito.times(1)).moveAtPercent(0);
        Mockito.verify(turn, Mockito.times(1)).moveToPosition(0.25);
    }



    @Test
    void commandSetpointValuesTurn2() {
        var module = new DriveModule(drive, turn, driveEncoder, filteredEncoder, logger);

        // encoder is at 10PI rads (5 full turns), module told to go to 0 m/s, 90 degrees
        when(turnEncoder.getEncoderPositionAccumulated_radians()).thenReturn(10 * Math.PI);
        module.commandSetpointValues(0, Math.toRadians(90));

        // should not drive, but should rotate the motor 90 degrees in sensor units, accounting for the current
        // sensor position of 10PI (2048 sensor units * 5 full rotations)
        Mockito.verify(drive, Mockito.times(1)).moveAtPercent(0); //5 rots + 0.25 = 5.25
        Mockito.verify(turn, Mockito.times(1)).moveToPosition(5.25);
    }
}