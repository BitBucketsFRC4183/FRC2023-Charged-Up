package org.bitbuckets.vision;

import edu.wpi.first.math.geometry.Pose3d;

import java.util.Optional;

public interface IVisionControl {

    Optional<Pose3d> estimateBestVisionTarget();
    Optional<Pose3d> estimateVisionRobotPose();
    Optional<Pose3d> estimateLoadLeftTarget();
    Optional<Pose3d> estimateLoadRightTarget();
    Optional<Pose3d> estimateConeLeftTarget();
    Optional<Pose3d> estimateConeRightTarget();

}

