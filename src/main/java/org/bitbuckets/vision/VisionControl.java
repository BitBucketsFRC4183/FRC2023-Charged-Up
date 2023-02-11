package org.bitbuckets.vision;

import edu.wpi.first.apriltag.AprilTagFieldLayout;
import edu.wpi.first.math.geometry.*;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.bitbuckets.lib.log.Debuggable;
import org.bitbuckets.lib.log.ILoggable;
import org.photonvision.EstimatedRobotPose;
import org.photonvision.PhotonCamera;
import org.photonvision.PhotonPoseEstimator;
import org.photonvision.PhotonUtils;
import org.photonvision.targeting.PhotonPipelineResult;
import org.photonvision.targeting.PhotonTrackedTarget;

import java.util.Optional;

public class VisionControl implements IVisionControl {


    final Transform3d robotToCamera;
    final PhotonCamera photonCamera;
    final AprilTagFieldLayout aprilTagFieldLayout;
    final PhotonPoseEstimator photonPoseEstimator;

    final Debuggable debuggable;


    VisionControl(Transform3d robotToCamera, AprilTagFieldLayout aprilTagFieldLayout, PhotonPoseEstimator photonPoseEstimator, PhotonCamera photonCamera, Debuggable debuggable) {
        this.robotToCamera = robotToCamera;
        this.aprilTagFieldLayout = aprilTagFieldLayout;
        this.photonPoseEstimator = photonPoseEstimator;
        this.photonCamera = photonCamera;
        this.debuggable = debuggable;
    }


    public void logLoop() {
        debuggable.log("a", "a");

        var opt = estimateTargetPose();
        opt.ifPresent(pose3d -> debuggable.log("target-pose", pose3d));
        var op2 = estimateRobotPose();
        op2.ifPresent(pose3d -> debuggable.log("robot-pose", pose3d));

    }


    @Override
    public Optional<Pose3d> estimateTargetPose() {
        return visionPoseEstimator().map(r -> r.goalPose);
    }

    @Override
    public Optional<Pose3d> estimateRobotPose() {
        return visionPoseEstimator().map(r -> r.robotPose);
    }



    public Optional<PhotonCalculationResult> visionPoseEstimator() {
        PhotonPipelineResult result = photonCamera.getLatestResult();
        if (!result.hasTargets()) return Optional.empty();
        PhotonTrackedTarget aprilTagTarget = result.getBestTarget();

        Transform3d transformToTag = aprilTagTarget.getBestCameraToTarget();
        int tagID = aprilTagTarget.getFiducialId();

        debuggable.log("tag-id", tagID);
        debuggable.log("transform-to-tag-from-origin", new Pose3d().transformBy(transformToTag));


        //Pose3d robotPose = PhotonUtils.estimateFieldToRobotAprilTag(tagPose, VisionConstants.aprilTags.get(tagID), robotToCamera);
        Optional<EstimatedRobotPose> robotPose3d = photonPoseEstimator.update();

        // load the april tag pose for this tagId
        var aprilTagPose = aprilTagFieldLayout.getTagPose(tagID);
        if (aprilTagPose.isEmpty()) return Optional.empty();

        Pose3d estimatedFieldRobotPose = PhotonUtils.estimateFieldToRobotAprilTag(transformToTag, aprilTagPose.get(), robotToCamera);
        SmartDashboard.putString("robotPOSE", estimatedFieldRobotPose.toString());
        // Transform the robot's pose to find the camera's pose
        var cameraPose = estimatedFieldRobotPose.transformBy(robotToCamera);

        SmartDashboard.putString("tagpose", transformToTag.toString());
        // Trasnform the camera's pose to the target's pose
        Pose3d targetPose = cameraPose.transformBy(transformToTag);

        // Transform the tag's pose to set our goal
        Pose3d goalPose = targetPose.transformBy(VisionConstants2.TAG_TO_GOAL);
        // This is new target data, so recalculate the goal
        double range = PhotonUtils.calculateDistanceToTargetMeters(
                VisionConstants2.CAMERA_HEIGHT,
                VisionConstants2.TAG_HEIGHT,
                VisionConstants2.CAMERA_PITCH,
                Units.degreesToRadians(aprilTagTarget.getPitch())
        );

        Translation2d translationToTag = PhotonUtils.estimateCameraToTargetTranslation(
                range, Rotation2d.fromDegrees(-aprilTagTarget.getYaw())
        );

        if (robotPose3d.isEmpty()) return Optional.empty();
        Pose3d currentEstimatedPose3d = robotPose3d.get().estimatedPose;
        Pose2d currentEstimatedPose2d = currentEstimatedPose3d.toPose2d();

        Pose3d tagPossiblePose3d = aprilTagFieldLayout.getTagPose(aprilTagTarget.getFiducialId()).orElseThrow();
        Pose2d tagPossiblePose2d = tagPossiblePose3d.toPose2d();

        Rotation2d targetYaw = PhotonUtils.getYawToPose(currentEstimatedPose2d, goalPose.toPose2d());
        SmartDashboard.putString("targetYaw", targetYaw.toString());
        return Optional.of(new PhotonCalculationResult(estimatedFieldRobotPose, goalPose, translationToTag, targetYaw, targetYaw.getRadians()));

    }
}