package org.bitbuckets.vision;

import edu.wpi.first.apriltag.AprilTagFieldLayout;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Rotation3d;
import edu.wpi.first.math.geometry.Transform3d;
import edu.wpi.first.math.geometry.Translation3d;
import edu.wpi.first.math.util.Units;
import org.bitbuckets.lib.core.HasLogLoop;
import org.bitbuckets.lib.debug.IDebuggable;
import org.photonvision.PhotonCamera;
import org.photonvision.PhotonPoseEstimator;

import java.util.Optional;

public class VisionControl implements IVisionControl, HasLogLoop {

    final PhotonCamera camera;
    final AprilTagFieldLayout layout;
    final PhotonPoseEstimator estimator;
    final IDebuggable debuggable;

    public VisionControl(PhotonCamera camera, AprilTagFieldLayout layout, PhotonPoseEstimator estimator, IDebuggable debuggable) {
        this.camera = camera;
        this.layout = layout;
        this.estimator = estimator;
        this.debuggable = debuggable;
    }

    //PhotonTrackedTarget cachedTarget = null;
    //int usedCachedTargetCounter = 0;

    @Override
    public Optional<Pose3d> estimateBestVisionTarget() {

        //TODO new fn to check what we want
        return Optional.ofNullable(
                camera.getLatestResult().getBestTarget()
        ).flatMap(tgt -> layout.getTagPose(tgt.getFiducialId()));



    }

    public Optional<Pose3d> estimateLoadLeftTarget() {
        return estimateBestVisionTarget().map(r-> r.transformBy(new Transform3d(new Translation3d(Units.inchesToMeters(14.55), Units.inchesToMeters(14.53), 0), new Rotation3d())));
    }

    public Optional<Pose3d> estimateLoadRightTarget() {
        return estimateBestVisionTarget().map(r-> r.transformBy(new Transform3d(new Translation3d(Units.inchesToMeters(14.55), Units.inchesToMeters(14.53), 0), new Rotation3d())));
    }

    public Optional<Pose3d> estimateConeLeftTarget() {
        return estimateBestVisionTarget().map(r-> r.transformBy(new Transform3d(new Translation3d(Units.inchesToMeters(14.55), Units.inchesToMeters(14.53), 0), new Rotation3d())));
    }

    public Optional<Pose3d> estimateConeRightTarget() {
        return estimateBestVisionTarget().map(r-> r.transformBy(new Transform3d(new Translation3d(Units.inchesToMeters(14.55), Units.inchesToMeters(14.53), 0), new Rotation3d())));
    }

    @Override
    public Optional<Pose3d> estimateVisionRobotPose() {
        return estimator.update(camera.getLatestResult()).map(poseDat -> poseDat.estimatedPose);
    }

    @Override
    public void logLoop() {
        var best = estimateBestVisionTarget();

        best.ifPresent(pose3d -> debuggable.log("target", pose3d.toPose2d()));
    }
}
