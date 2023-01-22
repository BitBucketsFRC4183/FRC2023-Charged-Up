package org.bitbuckets.vision;

import edu.wpi.first.math.controller.HolonomicDriveController;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.geometry.Transform3d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj.DataLogManager;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.bitbuckets.lib.ProcessPath;
import org.photonvision.PhotonCamera;
import org.photonvision.PhotonUtils;
import org.photonvision.targeting.PhotonPipelineResult;
import org.photonvision.targeting.PhotonTrackedTarget;

import java.util.List;

public class VisionControl {

    PhotonCamera camera = new PhotonCamera("Arducam_OV9281_USB_Camera");
    public void Localization() {
        PhotonPipelineResult result = camera.getLatestResult();
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
        double range = 0;
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



    public void teleopPeriodic() {
        Localization();
    }
}
