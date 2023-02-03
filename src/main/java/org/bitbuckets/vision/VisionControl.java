package org.bitbuckets.vision;

import edu.wpi.first.apriltag.AprilTagFieldLayout;
import edu.wpi.first.math.controller.HolonomicDriveController;
import edu.wpi.first.math.geometry.*;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.util.Units;
import org.bitbuckets.lib.log.ILoggable;
import org.photonvision.EstimatedRobotPose;
import org.photonvision.PhotonCamera;
import org.photonvision.PhotonPoseEstimator;
import org.photonvision.PhotonUtils;
import org.photonvision.targeting.PhotonPipelineResult;
import org.photonvision.targeting.PhotonTrackedTarget;

import java.util.Optional;

public class VisionControl {



    final Transform3d robotToCamera;
    final Pose2d pose2d;
    final PhotonCamera photonCamera;
    final AprilTagFieldLayout aprilTagFieldLayout;
    final PhotonPoseEstimator photonPoseEstimator;
    final ILoggable<double[]> loggable;

    final ILoggable<Translation2d[]> loggable2;


    VisionControl(Transform3d robotToCamera, Pose2d pose2d, AprilTagFieldLayout aprilTagFieldLayout, PhotonPoseEstimator photonPoseEstimator, PhotonCamera photonCamera, ILoggable<double[]> loggable, ILoggable<Translation2d[]> loggable2) {
        this.robotToCamera = robotToCamera;
        this.pose2d = pose2d;
        this.aprilTagFieldLayout = aprilTagFieldLayout;
        this.photonPoseEstimator = photonPoseEstimator;
        this.photonCamera = photonCamera;
        this.loggable = loggable;
        this.loggable2 = loggable2;
    }

    class PhotonCalculationResult {
        final Pose3d robotPose;
        final Translation2d translationToTag;
        final Rotation2d targetYaw;
        public PhotonCalculationResult(Pose3d robotPose, Translation2d translationToTag, Rotation2d targetYaw) {
            this.robotPose = robotPose;
            this.translationToTag = translationToTag;
            this.targetYaw = targetYaw;
        }
    }

    ;
    public Optional<PhotonCalculationResult> visionPoseEstimator() {

        PhotonPipelineResult result = photonCamera.getLatestResult();
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



        double range = PhotonUtils.calculateDistanceToTargetMeters(
                VisionConstants2.CAMERA_HEIGHT,
                VisionConstants2.TAG_HEIGHT,
                VisionConstants2.CAMERA_PITCH,
                Units.degreesToRadians(aprilTagTarget.getPitch())
        );

        Translation2d translationToTag = PhotonUtils.estimateCameraToTargetTranslation(
                range, Rotation2d.fromDegrees(-aprilTagTarget.getYaw())
        );

        loggable.log(new double[] {yaw, pitch, area, skew, range});
        loggable2.log(new Translation2d[] {translationToTag});

        Pose3d robotPose = PhotonUtils.estimateFieldToRobotAprilTag(tagPose, VisionConstants.aprilTags.compute(aprilTagTarget.getFiducialId(),(a,b) -> b), robotToCamera);

        Optional<EstimatedRobotPose> robotPose2 = photonPoseEstimator.update();

        if (robotPose2.isEmpty()) return Optional.empty();
        Pose3d pose = robotPose2.get().estimatedPose;

        Pose3d tagPossiblePose3d = aprilTagFieldLayout.getTagPose(aprilTagTarget.getFiducialId()).orElseThrow();
        Pose2d tagPossiblePose2d = tagPossiblePose3d.toPose2d();

        Rotation2d targetYaw = PhotonUtils.getYawToPose(pose2d, tagPossiblePose2d);



        return Optional.of(new PhotonCalculationResult(robotPose, translationToTag, targetYaw));










        }





    public void chaseTag() {


    }

    public void driveToPosition(ChassisSpeeds chassisSpeeds) {
        //controller.calculate(

      //  )


    }

    public void teleopPeriodic() {
        visionPoseEstimator();
        chaseTag();
    }
}
