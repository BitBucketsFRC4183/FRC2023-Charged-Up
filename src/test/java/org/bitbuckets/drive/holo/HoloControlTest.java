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

    DriveControl driveControl;
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
    void calculatePose2DMoveForwards() {
        // our pose is 0,0 rotated 0º (facing forwards, away from alliance wall)
        when(odometryControl.estimateFusedPose2d()).thenReturn(new Pose2d(0, 0, Rotation2d.fromDegrees(0)));

        // get chassis speeds for a target that is at 1, 0
        // we should get chassisSpeeds telling our robot to move forwards, away from the alliance wall
        var chassisSpeeds = control.calculatePose2D(
                new Pose2d(1, 0, Rotation2d.fromDegrees(0)), Rotation2d.fromDegrees(0),
                1);
        assertEquals(2, chassisSpeeds.vxMetersPerSecond, .1);
        assertEquals(0, chassisSpeeds.vyMetersPerSecond, .1);
        assertEquals(0, chassisSpeeds.omegaRadiansPerSecond, .1);
    }

    @Test
    void calculatePose2DMoveBackwards() {
        // our pose is 0,0 rotated 180º (facing the alliance wall)
        when(odometryControl.estimateFusedPose2d()).thenReturn(new Pose2d(0, 0, Rotation2d.fromDegrees(180)));

        // get chassis speeds for a target that is at 1, 0
        // we should get chassisSpeeds telling our robot to move backwards, away from the alliance wall
        var chassisSpeeds = control.calculatePose2D(
                new Pose2d(1, 0, Rotation2d.fromDegrees(0)), Rotation2d.fromDegrees(180),
                1);
        assertEquals(-2, chassisSpeeds.vxMetersPerSecond, .1);
        assertEquals(0, chassisSpeeds.vyMetersPerSecond, .1);
        assertEquals(0, chassisSpeeds.omegaRadiansPerSecond, .1);
    }


}