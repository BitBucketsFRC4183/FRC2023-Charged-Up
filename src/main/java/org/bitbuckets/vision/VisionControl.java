package org.bitbuckets.vision;

import edu.wpi.first.math.controller.HolonomicDriveController;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.geometry.Transform3d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.trajectory.TrapezoidProfile;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.bitbuckets.drive.module.AutoConstants;
import org.bitbuckets.drive.old.OldDriveSubsystem;
import org.bitbuckets.lib.hardware.PIDIndex;
import org.photonvision.PhotonUtils;
import org.photonvision.targeting.PhotonPipelineResult;
import org.photonvision.targeting.PhotonTrackedTarget;

import java.util.List;

public class VisionControl {

    OldDriveSubsystem oldDriveSubsystem;

    double range;

    double yaw;

    ;
    public void Localization() {
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
            Transform3d pose = target.getBestCameraToTarget();
            poseX = pose.getX();
            tagID = target.getFiducialId();


            range = PhotonUtils.calculateDistanceToTargetMeters(
                    cameraHeight,
                    tagHeight,
                    cameraPitch,
                    Units.degreesToRadians(result.getBestTarget().getPitch()));



        }
        SmartDashboard.putNumber("yaw", yaw);
        SmartDashboard.putNumber("pitch", pitch);
        SmartDashboard.putNumber("area", area);
        SmartDashboard.putNumber("skew", skew);
        SmartDashboard.putNumber("pose", poseX);
        SmartDashboard.putNumber("range", range);


    }

    public void chaseTag() {
        if (range > 0.3) {
            oldDriveSubsystem.driveBack();
            }
        else if (range < 0.3) {
            oldDriveSubsystem.driveForward();
        }


        if ( yaw > 3) {
            oldDriveSubsystem.driveLeft();
        }
        else if (yaw < 3) {
            oldDriveSubsystem.driveRight();
        }

    }

    HolonomicDriveController controller = new HolonomicDriveController(new PIDController(AutoConstants.pathXYPID[PIDIndex.P],AutoConstants.pathXYPID[PIDIndex.I],AutoConstants.pathXYPID[PIDIndex.D]),new PIDController(AutoConstants.pathXYPID[PIDIndex.P], AutoConstants.pathXYPID[PIDIndex.I], AutoConstants.pathXYPID[PIDIndex.D]),new ProfiledPIDController(AutoConstants.pathThetaPID[PIDIndex.P], AutoConstants.pathThetaPID[PIDIndex.I], AutoConstants.pathThetaPID[PIDIndex.D], new TrapezoidProfile.Constraints(AutoConstants.maxPathFollowVelocity, AutoConstants.maxPathFollowAcceleration)));

    public void driveToPosition(ChassisSpeeds chassisSpeeds) {
        //controller.calculate(

      //  )


    }

    public void teleopPeriodic() {
        Localization();
    }
}
