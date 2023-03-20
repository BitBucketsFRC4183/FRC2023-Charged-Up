package org.bitbuckets.drive.holo;

import edu.wpi.first.math.controller.HolonomicDriveController;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.trajectory.TrapezoidProfile;
import org.bitbuckets.drive.controlsds.DriveControl;
import org.bitbuckets.lib.log.IDebuggable;
import org.bitbuckets.odometry.IOdometryControl;
import org.bitbuckets.vision.IVisionControl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class HoloControlTest {

/*    DriveControl driveControl;
    IOdometryControl odometryControl;
    IVisionControl visionControl;

    IDebuggable debuggable;

    HoloControl control;

    @BeforeEach
    void setup() {
        driveControl = mock(DriveControl.class);
        odometryControl = mock(IOdometryControl.class);
        visionControl = mock(IVisionControl.class);
        debuggable = mock(IDebuggable.class);

        control = new HoloControl(driveControl, odometryControl,
                new HolonomicDriveController(new PIDController(1, 0, 0), new PIDController(1, 0, 0),
                        new ProfiledPIDController(1, 0, 0, new TrapezoidProfile.Constraints(1, 1)))
                , debuggable);
    }

    @Test
    void calculatePose2D() {
        // our estimated pose is 0,0,0
        when(odometryControl.estimatePose_trueFieldPose()).thenReturn(new Pose2d(0, 0, Rotation2d.fromDegrees(0)));

        // get chassis speeds for a target that is at 1, 0
        var chassisSpeeds = control.calculateTrueFieldControlEffort(
                new Pose2d(1, 0, Rotation2d.fromDegrees(0)),
                1);
        assertEquals(2, chassisSpeeds.vxMetersPerSecond);
        assertEquals(0, chassisSpeeds.vyMetersPerSecond);
        assertEquals(0, chassisSpeeds.omegaRadiansPerSecond);
    }*/


}