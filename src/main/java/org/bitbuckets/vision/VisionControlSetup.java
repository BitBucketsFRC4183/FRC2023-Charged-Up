package org.bitbuckets.vision;

import edu.wpi.first.apriltag.AprilTagFieldLayout;
import edu.wpi.first.math.geometry.Pose3d;
import org.bitbuckets.lib.IProcess;
import org.bitbuckets.lib.ISetup;
import org.photonvision.PhotonCamera;
import org.photonvision.PhotonPoseEstimator;
import org.photonvision.SimVisionSystem;

import java.util.function.Supplier;

public class VisionControlSetup implements ISetup<IVisionControl> {


    final Supplier<Pose3d> simulatedPoseSupplier;
    final String cameraName;
    final AprilTagFieldLayout fieldLayout;
    final VisionConfig visionConfig;
    final PhotonPoseEstimator.PoseStrategy strategy;

    public VisionControlSetup(Supplier<Pose3d> simulatedPoseSupplier, String cameraName, AprilTagFieldLayout fieldLayout, VisionConfig visionConfig, PhotonPoseEstimator.PoseStrategy strategy) {
        this.simulatedPoseSupplier = simulatedPoseSupplier;
        this.cameraName = cameraName;
        this.fieldLayout = fieldLayout;
        this.visionConfig = visionConfig;
        this.strategy = strategy;
    }

    @Override
    public VisionControl build(IProcess self) {


            PhotonCamera photonCamera = new PhotonCamera(cameraName);


        if (!self.isReal()) {
            //i  have no idea why this works.
            //they use the networktables instance to store state?
            //frc coders are wild
            SimVisionSystem system = new SimVisionSystem(
                    cameraName,
                    visionConfig.cameraFOV_degrees,
                    visionConfig.robotToCamera,
                    visionConfig.maxLEDRange,
                    visionConfig.cameraResWidth,
                    visionConfig.cameraResHeight,
                    visionConfig.minTargetArea
            );

            system.addVisionTargets(fieldLayout);

            self.registerLogicLoop(() -> {
                system.processFrame(simulatedPoseSupplier.get());
            });
        }

        PhotonPoseEstimator estimator = new PhotonPoseEstimator(
                fieldLayout,
                strategy,
                photonCamera,
                visionConfig.robotToCamera
        );


        return new VisionControl(
                photonCamera,
                fieldLayout,
                estimator,
                self.getDebuggable()
        );
    }
}
