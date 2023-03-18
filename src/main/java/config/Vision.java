package config;

import edu.wpi.first.apriltag.AprilTagFieldLayout;
import edu.wpi.first.apriltag.AprilTagFields;
import edu.wpi.first.math.geometry.Rotation3d;
import edu.wpi.first.math.geometry.Transform3d;
import edu.wpi.first.math.geometry.Translation3d;
import edu.wpi.first.math.util.Units;
import org.bitbuckets.vision.VisionConfig;
import org.photonvision.PhotonPoseEstimator;

import java.io.IOException;

public interface Vision {

    String CAMERA_NAME = "Arducam_OV9281_USB_Camera";

    VisionConfig CONFIG = new VisionConfig(
        75.0,
        new Transform3d(
              new Translation3d(Units.inchesToMeters(13), 0, Units.inchesToMeters(11.5)),
              new Rotation3d()
        ),
        20,
        640,
        480,
        10,
        Units.inchesToMeters(24.63),
        Units.inchesToMeters(30),
        Units.degreesToRadians(350)
    );

    PhotonPoseEstimator.PoseStrategy STRATEGY = PhotonPoseEstimator.PoseStrategy.LOWEST_AMBIGUITY;

    Transform3d TAG_TO_CUBE =
            new Transform3d(
                    new Translation3d(0.5, 0, 0.0),
                    new Rotation3d(0.0, 0, Math.PI));
    Transform3d TAG_TO_LOAD_LEFT = new Transform3d(
            new Translation3d(0.5, 0.2, 0.0),
            new Rotation3d(0.0, 0.0, Math.PI));
    Transform3d TAG_TO_LOAD_RIGHT  =new Transform3d(
            new Translation3d(0.0, -0.2, 0.0),
            new Rotation3d(0.0, 0.0, Math.PI));
    Transform3d TAG_TO_CONE_LEFT = new Transform3d(
            new Translation3d(0.5, 0.2, 0.0),
            new Rotation3d(0.0, 0.0, Math.PI));
    Transform3d TAG_TO_CONE_RIGHT = new Transform3d(
            new Translation3d(0.5, -0.2, 0.0),
            new Rotation3d(0.0, 0.0, Math.PI));


    //TODO this ruins the whole point of setups
    class LAYOUTCONTAINER {
        public static AprilTagFieldLayout LAYOUT;

        static {
            try {
                LAYOUT = AprilTagFieldLayout.loadFromResource(AprilTagFields.k2023ChargedUp.m_resourceFile);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }



    ;

}
