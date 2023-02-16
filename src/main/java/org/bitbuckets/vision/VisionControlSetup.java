package org.bitbuckets.vision;

import edu.wpi.first.apriltag.AprilTagFieldLayout;
import edu.wpi.first.apriltag.AprilTagFields;
import edu.wpi.first.math.geometry.Rotation3d;
import edu.wpi.first.math.geometry.Transform3d;
import edu.wpi.first.math.geometry.Translation3d;
import edu.wpi.first.math.util.Units;
import org.bitbuckets.lib.IProcess;
import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.log.IDebuggable;
import org.bitbuckets.lib.util.MockingUtil;
import org.photonvision.PhotonCamera;
import org.photonvision.PhotonPoseEstimator;

import java.io.IOException;

public class VisionControlSetup implements ISetup<IVisionControl> {

    final boolean isEnabled;

    public VisionControlSetup(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }


    @Override
    public IVisionControl build(IProcess self) {

        if (!isEnabled) {
            return MockingUtil.buddy(VisionControl.class);
        }

        AprilTagFieldLayout aprilTagFieldLayout;
        try {
            aprilTagFieldLayout = AprilTagFieldLayout.loadFromResource(AprilTagFields.k2023ChargedUp.m_resourceFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        //TODO find the offset from robot to camera
        Transform3d robotToCamera = new Transform3d(new Translation3d(Units.inchesToMeters(13), 0, Units.inchesToMeters(11.5)), new Rotation3d());
        PhotonCamera photonCamera = new PhotonCamera("Arducam_OV9281_USB_Camera");
        PhotonPoseEstimator photonPoseEstimator = new PhotonPoseEstimator(aprilTagFieldLayout, PhotonPoseEstimator.PoseStrategy.LOWEST_AMBIGUITY, photonCamera, robotToCamera);
        IDebuggable debuggable = self.getDebuggable();

        VisionControl control = new VisionControl(robotToCamera, aprilTagFieldLayout, photonPoseEstimator, photonCamera, debuggable);

        self.registerLogLoop(control::logLoop);

        return control;
    }

}
