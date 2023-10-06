package org.bitbuckets.vision;

import edu.wpi.first.apriltag.AprilTagFieldLayout;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Rotation2d;
import org.bitbuckets.OperatorInput;
import org.bitbuckets.lib.core.HasLogLoop;
import org.bitbuckets.lib.core.HasLoop;
import org.bitbuckets.lib.log.IDebuggable;
import org.photonvision.PhotonCamera;
import org.photonvision.PhotonPoseEstimator;

import java.util.Optional;

public class VisionControl implements IVisionControl, HasLogLoop, HasLoop {

    final PhotonCamera camera;
    final AprilTagFieldLayout layout;
    final PhotonPoseEstimator estimator;
    final IDebuggable debuggable;
    final OperatorInput operatorInput;

    public VisionControl(PhotonCamera camera, AprilTagFieldLayout layout, PhotonPoseEstimator estimator, OperatorInput operatorInput, IDebuggable debuggable) {
        this.camera = camera;
        this.layout = layout;
        this.estimator = estimator;
        this.operatorInput = operatorInput;
        this.debuggable = debuggable;
    }

    //PhotonTrackedTarget cachedTarget = null;
    //int usedCachedTargetCounter = 0;

    Pose3d locked;
    Pose3d currentlyLookingAt;

    @Override
    public Optional<Pose3d> estimateBestVisionTarget() {
        return Optional.ofNullable(locked);


    }

    @Override
    public Optional<Pose3d> estimateVisionRobotPose() {
        return estimator.update(camera.getLatestResult()).map(poseDat -> poseDat.estimatedPose);
    }

    @Override
    public void logLoop() {
        //debuggable.log("lockedStatus", locked != null);
        debuggable.log("currentlyLookingAtStatus", currentlyLookingAt != null);
        debuggable.log("currentlyLookingAt", currentlyLookingAt.toPose2d());
        if (locked != null) {
            debuggable.log("target", locked.toPose2d());
        }
        else {
            debuggable.log("target", new Pose2d(0,0, new Rotation2d()));
        }
    }

    boolean lastValueOfDriveEnabled = false;

    @Override
    public void loop() {
        boolean currentValueOfDriveEnabled = operatorInput.isVisionDrivePressed();

        if (currentValueOfDriveEnabled == false && lastValueOfDriveEnabled == true) {
            //released

            locked = null;

        }

        if (camera.getLatestResult().hasTargets()) {
            int id = camera.getLatestResult().getBestTarget().getFiducialId();
            currentlyLookingAt = layout.getTagPose(id).orElse(new Pose3d());
        }

        if (currentValueOfDriveEnabled == true && lastValueOfDriveEnabled == false && locked == null) {


            if (camera.getLatestResult().hasTargets()) {
                int id = camera.getLatestResult().getBestTarget().getFiducialId();
                locked = layout.getTagPose(id).orElse(new Pose3d());
            }

        }

        lastValueOfDriveEnabled = currentValueOfDriveEnabled;

    }
}
