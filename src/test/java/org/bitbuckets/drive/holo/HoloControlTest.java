package org.bitbuckets.drive.holo;

import com.pathplanner.lib.PathPlannerTrajectory;
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
    void calculatePose2D() {
        // our estimated pose is 0,0,0
        when(odometryControl.estimateFusedPose2d()).thenReturn(new Pose2d(0, 0, Rotation2d.fromDegrees(0)));

        // get chassis speeds for a target that is at 1, 0
        var chassisSpeeds = control.calculatePose2D(
                new Pose2d(1, 0, Rotation2d.fromDegrees(0)),
                1,
                Rotation2d.fromDegrees(0)
        );
        assertEquals(2, chassisSpeeds.vxMetersPerSecond);
        assertEquals(0, chassisSpeeds.vyMetersPerSecond);
        assertEquals(0, chassisSpeeds.omegaRadiansPerSecond);
    }

    @Test
    void calculateTrajectoryState() {
        // our robot starts at 4,4 with no rotation
        when(odometryControl.estimateFusedPose2d()).thenReturn(new Pose2d(4, 4, Rotation2d.fromDegrees(0)));

        // this is from our test-path in pathplanner
        // we go from (4,4) to (6,4) with a -90º heading. The heading means
        // our robot moves up a little as it moves forward but it shouldn't rotate at all
        var state = new PathPlannerTrajectory.PathPlannerState();
        state.poseMeters = new Pose2d(6, 4, Rotation2d.fromDegrees(-90));
        state.holonomicRotation = Rotation2d.fromDegrees(0);
        state.velocityMetersPerSecond = 1;
        state.accelerationMetersPerSecondSq = 1;

        var chassisSpeeds = control.calculatePose2DFromState(state);
        assertEquals(2, chassisSpeeds.vxMetersPerSecond);
        assertEquals(-1, chassisSpeeds.vyMetersPerSecond);
        assertEquals(0, chassisSpeeds.omegaRadiansPerSecond);
    }

    @Test
    void calculateTrajectoryState2() {
        // for the second part of our test auto path, we are at (6, 4) and moving towards (6,6)
        when(odometryControl.estimateFusedPose2d()).thenReturn(new Pose2d(6, 4, Rotation2d.fromDegrees(0)));

        // this is from our test-path in pathplanner
        // we go from (6,4) to (6,6) with a -90º heading. The heading means
        // our robot moves straight down but shouldn't rotate at all
        var state = new PathPlannerTrajectory.PathPlannerState();
        state.poseMeters = new Pose2d(6, 6, Rotation2d.fromDegrees(-90));
        state.holonomicRotation = Rotation2d.fromDegrees(0);
        state.velocityMetersPerSecond = 1;
        state.accelerationMetersPerSecondSq = 1;

        var chassisSpeeds = control.calculatePose2DFromState(state);
        assertEquals(0, chassisSpeeds.vxMetersPerSecond, .01);
        assertEquals(1, chassisSpeeds.vyMetersPerSecond);
        assertEquals(0, chassisSpeeds.omegaRadiansPerSecond);
    }
}