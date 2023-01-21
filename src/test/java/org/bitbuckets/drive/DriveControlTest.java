package org.bitbuckets.drive;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import org.bitbuckets.drive.control.DriveControl;
import org.bitbuckets.drive.control.DriveControlDataAutoGen;
import org.bitbuckets.drive.module.DriveModule;
import org.bitbuckets.lib.log.DataLogger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * TODO more than just mocking tests
 */
public class DriveControlTest {

    @Test
    public void reportActualStates_shouldReflectCurrentStatesStates() {
        DriveModule drive1 = Mockito.mock(DriveModule.class);
        DriveModule drive2 = Mockito.mock(DriveModule.class);
        DriveModule drive3 = Mockito.mock(DriveModule.class);
        DriveModule drive4 = Mockito.mock(DriveModule.class);
        DataLogger<DriveControlDataAutoGen> logger = Mockito.mock(DataLogger.class);

        Mockito.when(drive1.reportState()).thenReturn(new SwerveModuleState(5, Rotation2d.fromDegrees(180)));
        Mockito.when(drive2.reportState()).thenReturn(new SwerveModuleState(5, Rotation2d.fromDegrees(180)));
        Mockito.when(drive3.reportState()).thenReturn(new SwerveModuleState(5, Rotation2d.fromDegrees(180)));
        Mockito.when(drive4.reportState()).thenReturn(new SwerveModuleState(5, Rotation2d.fromDegrees(180)));

        DriveControl control = new DriveControl(logger, drive1, drive2, drive3, drive4);

        SwerveModuleState[] states = control.reportActualStates();

        Assertions.assertEquals(5, states[0].speedMetersPerSecond);
        Assertions.assertEquals(180, states[1].angle.getDegrees(), 0.01);
    }

    @Test
    public void reportState_shouldReflectSetpoints() {
        DriveModule drive1 = Mockito.mock(DriveModule.class);
        DriveModule drive2 = Mockito.mock(DriveModule.class);
        DriveModule drive3 = Mockito.mock(DriveModule.class);
        DriveModule drive4 = Mockito.mock(DriveModule.class);
        DataLogger<DriveControlDataAutoGen> logger = Mockito.mock(DataLogger.class);
        Mockito.when(drive1.reportState()).thenReturn(new SwerveModuleState(0, Rotation2d.fromDegrees(0)));

        DriveControl control = new DriveControl(logger, drive1, drive2, drive3, drive4);
        control.doDriveWithStates(new SwerveModuleState[]{
                new SwerveModuleState(5, Rotation2d.fromDegrees(180))
        });

        Assertions.assertEquals(5, control.reportSetpointStates()[0].speedMetersPerSecond);


    }

}
