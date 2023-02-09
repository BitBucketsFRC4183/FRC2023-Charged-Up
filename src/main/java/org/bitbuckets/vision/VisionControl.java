package org.bitbuckets.vision;

import edu.wpi.first.apriltag.AprilTagFieldLayout;
import edu.wpi.first.math.geometry.*;
import edu.wpi.first.math.util.Units;
import org.bitbuckets.lib.log.ILoggable;
import org.photonvision.EstimatedRobotPose;
import org.photonvision.PhotonCamera;
import org.photonvision.PhotonPoseEstimator;
import org.photonvision.PhotonUtils;
import org.photonvision.targeting.PhotonPipelineResult;
import org.photonvision.targeting.PhotonTrackedTarget;

import java.util.Optional;

import static org.bitbuckets.vision.VisionConstants2.TAG_TO_CHASE;


public class VisionControl implements Runnable, IVisionControl {


    final Transform3d robotToCamera;
    final PhotonCamera photonCamera;
    final AprilTagFieldLayout aprilTagFieldLayout;
    final PhotonPoseEstimator photonPoseEstimator;

    final ILoggable<double[]> loggable;
    final ILoggable<Translation2d[]> loggable2;

    public VisionControl(Transform3d robotToCamera, AprilTagFieldLayout aprilTagFieldLayout, PhotonPoseEstimator photonPoseEstimator, PhotonCamera photonCamera, ILoggable<double[]> loggable, ILoggable<Translation2d[]> loggable2) {
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

    @Override
    public Optional<Pose3d> estimateTargetPose() {
        return visionPoseEstimator().map(pr -> pr.goalPose);
    }

    @Override
    public Optional<Pose3d> estimateRobotPose() {
        return visionPoseEstimator().map(pr -> pr.robotPose);
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
            // Find the tag we want to chase
            var targetOpt = result.getTargets().stream()
                    .filter(t -> t.getFiducialId() == TAG_TO_CHASE)
                    .filter(t -> !t.equals(aprilTagTarget) && t.getPoseAmbiguity() <= .2 && t.getPoseAmbiguity() != -1)
                    .findFirst();

            if (targetOpt.isPresent()) {
                var target = targetOpt.get();

                Pose3d robotPose = PhotonUtils.estimateFieldToRobotAprilTag(tagPose, VisionConstants.aprilTags.get(tagID), robotToCamera);

                // Transform the robot's pose to find the camera's pose
                var cameraPose = robotPose.transformBy(robotToCamera);

                // Trasnform the camera's pose to the target's pose
                var camToTarget = target.getBestCameraToTarget();
                var targetPose = cameraPose.transformBy(camToTarget);

                // Transform the tag's pose to set our goal
                var goalPose = targetPose.transformBy(VisionConstants2.TAG_TO_GOAL).toPose2d();


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


                //Pose3d robotPose = PhotonUtils.estimateFieldToRobotAprilTag(tagPose, VisionConstants.aprilTags.get(tagID), robotToCamera);

                Optional<EstimatedRobotPose> robotPose3d = photonPoseEstimator.update();

                if (robotPose3d.isEmpty()) return Optional.empty();
                Pose3d currentEstimatedPose3d = robotPose3d.get().estimatedPose;
                Pose2d currentEstimatedPose2d = currentEstimatedPose3d.toPose2d();

                Pose3d tagPossiblePose3d = aprilTagFieldLayout.getTagPose(aprilTagTarget.getFiducialId()).orElseThrow();
                Pose2d tagPossiblePose2d = tagPossiblePose3d.toPose2d();

                Rotation2d targetYaw = PhotonUtils.getYawToPose(currentEstimatedPose2d, tagPossiblePose2d);


                return Optional.of(new PhotonCalculationResult(robotPose, tagPossiblePose3d, translationToTag, targetYaw, targetYaw.getRadians()));


            }


            return Optional.empty();

            //public void driveToPosition (ChassisSpeeds chassisSpeeds){
            //controller.calculate(

            //  )


        }

        return Optional.empty();


    }
}
