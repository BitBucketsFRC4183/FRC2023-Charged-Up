package org.bitbuckets.vision;

import edu.wpi.first.apriltag.AprilTagFieldLayout;
import edu.wpi.first.apriltag.AprilTagFields;
import edu.wpi.first.math.geometry.*;
import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.ProcessPath;
import org.bitbuckets.lib.log.ILoggable;
import org.photonvision.PhotonCamera;
import org.photonvision.PhotonPoseEstimator;

import java.io.IOException;

public class VisionControlSetup implements ISetup<VisionControl> {
    @Override
    public VisionControl build(ProcessPath path) {
        AprilTagFieldLayout aprilTagFieldLayout;
        try {
            aprilTagFieldLayout = AprilTagFieldLayout.loadFromResource(AprilTagFields.k2023ChargedUp.m_resourceFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Transform3d robotToCamera = new Transform3d(new Translation3d(), new Rotation3d());
        Pose2d robotPose2d = new Pose2d();

        PhotonCamera photonCamera = new PhotonCamera("Arducam_OV9281_USB_Camera");
        ILoggable<double[]> loggable = path.generateDoubleLoggers("yaw", "pitch", "area","skew", "range");





        PhotonPoseEstimator photonPoseEstimator = new PhotonPoseEstimator(aprilTagFieldLayout, PhotonPoseEstimator.PoseStrategy.CLOSEST_TO_REFERENCE_POSE, photonCamera, robotToCamera);



        ILoggable<Translation2d[]> loggable2 = null;
        return new VisionControl(robotToCamera, aprilTagFieldLayout, photonPoseEstimator, photonCamera, loggable, loggable2);
    }

}
