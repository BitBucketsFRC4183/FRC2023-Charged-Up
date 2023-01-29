package org.bitbuckets.vision;

import edu.wpi.first.apriltag.AprilTagFieldLayout;
import edu.wpi.first.apriltag.AprilTagFields;
import edu.wpi.first.math.controller.HolonomicDriveController;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.geometry.*;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.trajectory.TrapezoidProfile;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.util.sendable.Sendable;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.bitbuckets.drive.module.AutoConstants;
import org.bitbuckets.drive.old.OldDriveSubsystem;
import org.bitbuckets.lib.hardware.PIDIndex;
import org.bitbuckets.odometry.OdometryControl;
import org.photonvision.PhotonPoseEstimator;
import org.photonvision.PhotonUtils;
import org.photonvision.RobotPoseEstimator;
import org.photonvision.targeting.PhotonPipelineResult;
import org.photonvision.targeting.PhotonTrackedTarget;


import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class VisionControl {

    OldDriveSubsystem oldDriveSubsystem;

    OdometryControl odometryControl;

    AprilTagFieldLayout aprilTagFieldLayout = AprilTagFieldLayout.loadFromResource(AprilTagFields.k2023ChargedUp.m_resourceFile);


    double range;

    double yaw;

    Pose3d robotPose;

    Translation2d translationToTag;

    Rotation2d targetYaw;

    Transform3d robotToCamera = new Transform3d(new Translation3d(), new Rotation3d());




    public VisionControl() throws IOException {
    }


    ;
    public void visionPoseEstimator() {
        PhotonPipelineResult result = VisionControlSetup.camera.getLatestResult();
        double yaw = 0;
        double pitch = 0;
        double area = 0;
        double skew = 0;
        double poseX = 0;
        int tagID;


        double tagHeight = Units.inchesToMeters(24.63);
        double cameraHeight = Units.inchesToMeters(20.5);
        double cameraPitch = Units.degreesToRadians(3.7);
        PhotonTrackedTarget target = result.getBestTarget();
        List<PhotonTrackedTarget> allTargets = result.getTargets();

        SmartDashboard.putNumber("size", allTargets.size());

        if (result.hasTargets()) {
            yaw = target.getYaw();
            pitch = target.getPitch();
            area = target.getArea();
            skew = target.getSkew();
            Transform3d tagPose = target.getBestCameraToTarget();
            poseX = tagPose.getX();
            tagID = target.getFiducialId();


            range = PhotonUtils.calculateDistanceToTargetMeters(
                    cameraHeight,
                    tagHeight,
                    cameraPitch,
                    Units.degreesToRadians(result.getBestTarget().getPitch()));

            translationToTag = PhotonUtils.estimateCameraToTargetTranslation(
                    range, Rotation2d.fromDegrees(-target.getYaw()));

            robotPose = PhotonUtils.estimateFieldToRobotAprilTag(tagPose, VisionConstants.aprilTags.compute(target.getFiducialId(),VisionConstants.aprilTags), robotToCamera);

            targetYaw = PhotonUtils.getYawToPose(odometryControl.swerveDrivePoseEstimator, aprilTagFieldLayout.getTagPose(target.getFiducialId()));

            PhotonPoseEstimator photonPoseEstimator = new PhotonPoseEstimator(aprilTagFieldLayout, PhotonPoseEstimator.PoseStrategy.CLOSEST_TO_REFERENCE_POSE, VisionControlSetup.camera, robotToCamera);

            photonPoseEstimator.update();





        }
        SmartDashboard.putNumber("yaw", yaw);
        SmartDashboard.putNumber("pitch", pitch);
        SmartDashboard.putNumber("area", area);
        SmartDashboard.putNumber("skew", skew);
        SmartDashboard.putNumber("pose", poseX);
        SmartDashboard.putNumber("range", range);
        SmartDashboard.putData("translation to tag", (Sendable) translationToTag);


    }

    public void chaseTag() {
        if (range > 0.3) {
            oldDriveSubsystem.driveBack();
            }
        else if (range < 0.3) {
            oldDriveSubsystem.driveForward();
        }


        if ( yaw > 0) {
            oldDriveSubsystem.driveLeft();
        }
        else if (yaw < 0) {
            oldDriveSubsystem.driveRight();
        }

    }

    HolonomicDriveController controller = new HolonomicDriveController(new PIDController(AutoConstants.pathXYPID[PIDIndex.P],AutoConstants.pathXYPID[PIDIndex.I],AutoConstants.pathXYPID[PIDIndex.D]),new PIDController(AutoConstants.pathXYPID[PIDIndex.P], AutoConstants.pathXYPID[PIDIndex.I], AutoConstants.pathXYPID[PIDIndex.D]),new ProfiledPIDController(AutoConstants.pathThetaPID[PIDIndex.P], AutoConstants.pathThetaPID[PIDIndex.I], AutoConstants.pathThetaPID[PIDIndex.D], new TrapezoidProfile.Constraints(AutoConstants.maxPathFollowVelocity, AutoConstants.maxPathFollowAcceleration)));

    public void driveToPosition(ChassisSpeeds chassisSpeeds) {
        //controller.calculate(

      //  )


    }

    public void teleopPeriodic() {
        visionPoseEstimator();
        chaseTag();
    }
}
