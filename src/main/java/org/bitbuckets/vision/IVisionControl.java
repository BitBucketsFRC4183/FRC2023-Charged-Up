package org.bitbuckets.vision;

import edu.wpi.first.math.geometry.Pose3d;

import java.util.Optional;

public interface IVisionControl {

    Optional<Pose3d> estimateTargetPose();
    Optional<Pose3d> estimateRobotPose();

}
