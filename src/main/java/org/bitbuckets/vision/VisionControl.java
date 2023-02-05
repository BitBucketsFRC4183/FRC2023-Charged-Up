package org.bitbuckets.vision;

import edu.wpi.first.apriltag.AprilTagFieldLayout;
import edu.wpi.first.math.geometry.*;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.bitbuckets.lib.log.ILoggable;
import org.photonvision.EstimatedRobotPose;
import org.photonvision.PhotonCamera;
import org.photonvision.PhotonPoseEstimator;
import org.photonvision.PhotonUtils;
import org.photonvision.targeting.PhotonPipelineResult;
import org.photonvision.targeting.PhotonTrackedTarget;

import java.util.Optional;


public class VisionControl implements Runnable {


    final Transform3d robotToCamera;
    final PhotonCamera photonCamera;
    final AprilTagFieldLayout aprilTagFieldLayout;
    final PhotonPoseEstimator photonPoseEstimator;

    final ILoggable<double[]> loggable;
    final ILoggable<Translation2d[]> loggable2;

    VisionControl(Transform3d robotToCamera, AprilTagFieldLayout aprilTagFieldLayout, PhotonPoseEstimator photonPoseEstimator, PhotonCamera photonCamera, ILoggable<double[]> loggable, ILoggable<Translation2d[]> loggable2) {
        this.robotToCamera = robotToCamera;
        this.aprilTagFieldLayout = aprilTagFieldLayout;
        this.photonPoseEstimator = photonPoseEstimator;
        this.photonCamera = photonCamera;
        this.loggable = loggable;
        this.loggable2 = loggable2;
    }

    @Override
    public void run() {
        //TODO log
    }

    public class PhotonCalculationResult {
        public final Pose3d robotPose;
        public final Pose3d goalPose;
        public final Translation2d translationToTag;
        public final Rotation2d targetYaw;
        public final double yaw;

        public PhotonCalculationResult(Pose3d robotPose, Pose3d goalPose, Translation2d translationToTag, Rotation2d targetYaw, double yaw) {
            this.robotPose = robotPose;
            this.goalPose = goalPose;
            this.translationToTag = translationToTag;
            this.targetYaw = targetYaw;
            this.yaw = yaw;
        }
    }


    public Optional<Pose3d> estimateRobotPose() {
        Optional<EstimatedRobotPose> result = photonPoseEstimator.update();
        if (result.isEmpty()) return Optional.empty();
        return Optional.of(result.get().estimatedPose);
    }

    public Optional<PhotonCalculationResult> visionPoseEstimator() {
        PhotonPipelineResult result = photonCamera.getLatestResult();
        if (!result.hasTargets()) return Optional.empty();

        PhotonTrackedTarget aprilTagTarget = result.getBestTarget();
        int count = result.targets.size();
        if (count == 0) return Optional.empty();

        double yaw = aprilTagTarget.getYaw();
        double pitch = aprilTagTarget.getPitch();
        double area = aprilTagTarget.getArea();
        double skew = aprilTagTarget.getSkew();
        Transform3d tagPose = aprilTagTarget.getBestCameraToTarget();
        double poseX = tagPose.getX();
        int tagID = aprilTagTarget.getFiducialId();

        if (result.hasTargets()) {
            //Pose3d robotPose = PhotonUtils.estimateFieldToRobotAprilTag(tagPose, VisionConstants.aprilTags.get(tagID), robotToCamera);
            Optional<EstimatedRobotPose> robotPose3d = photonPoseEstimator.update();

            Pose3d robotPose = PhotonUtils.estimateFieldToRobotAprilTag(tagPose, VisionConstants.aprilTags.get(tagID), robotToCamera);
            SmartDashboard.putString("robotPOSE", robotPose.toString());
            // Transform the robot's pose to find the camera's pose
            var cameraPose = robotPose.transformBy(robotToCamera);

            SmartDashboard.putString("tagpose", tagPose.toString());
            // Trasnform the camera's pose to the target's pose
            var targetPose = cameraPose.transformBy(tagPose);

            // Transform the tag's pose to set our goal
            var goalPose = targetPose.transformBy(VisionConstants2.TAG_TO_GOAL);
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
            return Optional.of(new PhotonCalculationResult(robotPose, goalPose, translationToTag, targetYaw, targetYaw.getRadians()));


            //  )


        } else {
            SmartDashboard.putString("tagpose", tagPose.toString());

        }

        return Optional.empty();


    }
}
