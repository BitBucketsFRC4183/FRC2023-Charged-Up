package org.bitbuckets.vision;

import edu.wpi.first.apriltag.AprilTagFieldLayout;
import edu.wpi.first.math.geometry.*;
import org.bitbuckets.lib.log.Debuggable;
import org.bitbuckets.lib.log.ILoggable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.photonvision.EstimatedRobotPose;
import org.photonvision.PhotonCamera;
import org.photonvision.PhotonPoseEstimator;
import org.photonvision.PhotonUtils;
import org.photonvision.targeting.PhotonPipelineResult;
import org.photonvision.targeting.PhotonTrackedTarget;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class VisionControlTest {

    PhotonPoseEstimator photonPoseEstimator;
    PhotonCamera photonCamera;

    PhotonPipelineResult photonPipelineResult;

    AprilTagFieldLayout aprilTagFieldLayout;

    VisionControl control;

    @BeforeEach
    void setUp() throws IOException {
        // mock out photon code
        photonPoseEstimator = mock(PhotonPoseEstimator.class);
        photonCamera = mock(PhotonCamera.class);
        photonPipelineResult = mock(PhotonPipelineResult.class);
        aprilTagFieldLayout = mock(AprilTagFieldLayout.class);

        when(photonCamera.getLatestResult()).thenReturn(photonPipelineResult);
        var robotToCameraTransform = new Transform3d();

        control = new VisionControl(
                robotToCameraTransform,
                aprilTagFieldLayout,
                photonPoseEstimator,
                photonCamera,
                mock(Debuggable.class));
    }

    @Test
    void estimateFieldToRobotAprilTag() {
        // assume camera and robot have no transform
        var cameraToRobot = new Transform3d();

        assertEquals(
                new Pose3d(),
                PhotonUtils.estimateFieldToRobotAprilTag(new Transform3d(), new Pose3d(), cameraToRobot)
        );

        // if our camera sees the target at (1, 0, 0) and the tag is at (0, 0, 0), that means we need to translate back 1 on the x
        var cameraToTarget = new Transform3d(new Translation3d(1, 0, 0), new Rotation3d());
        var fieldRelativeTagPose = new Pose3d();
        var expectedPose = new Pose3d(new Translation3d(-1, 0, 0), new Rotation3d());
        assertEquals(
                expectedPose,
                PhotonUtils.estimateFieldToRobotAprilTag(
                        cameraToTarget,
                        fieldRelativeTagPose,
                        cameraToRobot
                )
        );

        // if our camera sees the target at (1, 0, 0) and the tag is at (0, 0, 0), that means we need to translate back 1 on the x
        // this assumes PhotonVision is reporting the april tag at 180º, facing us.
        cameraToTarget = new Transform3d(new Translation3d(1, 0, 0), new Rotation3d(0, 0, Math.round(180)));
        fieldRelativeTagPose = new Pose3d(new Translation3d(), new Rotation3d(0, 0, Math.round(180)));
        expectedPose = new Pose3d(new Translation3d(-1, 0, 0), new Rotation3d());
        assertEquals(
                expectedPose,
                PhotonUtils.estimateFieldToRobotAprilTag(
                        cameraToTarget,
                        fieldRelativeTagPose,
                        cameraToRobot
                )
        );

    }

    @Test
    void visionPoseEstimatorNoTargets() {
        when(photonPipelineResult.hasTargets()).thenReturn(false);
        assertEquals(Optional.empty(), control.visionPoseEstimator());
    }

    @Test
    void visionPoseEstimator() {
        // make our vision return a target
        when(photonPipelineResult.hasTargets()).thenReturn(true);

        // Photon vision estimates that ouer robot is at 1, 0, 0 -> rotated 180º field relative
        EstimatedRobotPose robotPose = new EstimatedRobotPose(new Pose3d(2, 0, 0, new Rotation3d(0, 0, Math.toRadians(180))), 0);
        when(photonPoseEstimator.update()).thenReturn(Optional.of(robotPose));

        // Mock our apriltag to 0, 0, rotated 180 (facing towards us)
        var aprilTagPose = new Pose3d(new Translation3d(0, 0, 0), new Rotation3d(0, 0, Math.toRadians(180)));
        when(aprilTagFieldLayout.getTagPose(anyInt())).thenReturn(Optional.of(aprilTagPose));
        // we should need to move (-1, 0) to translate to our target april tag
        Transform3d cameraToTargetTransform = new Transform3d(new Translation3d(-2, 0, 0), new Rotation3d(0, 0, 0));

        PhotonTrackedTarget aprilTagTarget = mock(PhotonTrackedTarget.class);

        // we are at (1, 0, 0) facing the target, so the distance is 1
        var distance = 1d;
        // figure out our pitch given a distance of one
        var aprilTagPitchRadians = Math.atan((VisionConstants2.TAG_HEIGHT - VisionConstants2.CAMERA_HEIGHT) / distance);
        when(aprilTagTarget.getBestCameraToTarget()).thenReturn(cameraToTargetTransform);
        when(aprilTagTarget.getPitch()).thenReturn(Math.toDegrees(aprilTagPitchRadians));

        // assume our apriltag reports that it's rotated 180 degrees to face us
        var aprilTagYawDegrees = 180d;
        when(aprilTagTarget.getYaw()).thenReturn(aprilTagYawDegrees);
        when(photonPipelineResult.getBestTarget()).thenReturn(aprilTagTarget);

        // call the visionPoseEstimator
        var result = control.visionPoseEstimator();
        assertEquals(true, result.isPresent());

        // if we are at (2,0), the target is at (0, 0) and we are facing it
        // we expect the translation to target to be (-1, 0) and our rotation should stay at 180
        // we want to be move 1 towards the target
        var actual = result.get();
        assertEquals(new Pose3d(new Translation3d(-2, 0, 0), new Rotation3d(0, 0, Math.toRadians(180))), actual.robotPose);
        assertEquals(new Pose2d(new Translation2d(-1, 0), new Rotation2d(Math.toRadians(180))), actual.goalPose.toPose2d());
        assertEquals(-1, actual.translationToTag.getX(), .01);
        assertEquals(0, actual.translationToTag.getY(), .01);
        assertEquals(Math.toRadians(0), actual.targetYaw.getDegrees(), .01);
        assertEquals(Math.toRadians(0), actual.yaw, .01);

    }

}