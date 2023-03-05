package org.bitbuckets.vision;

import edu.wpi.first.math.geometry.Pose3d;

import java.util.Optional;

public interface IVisionControl {

    Optional<Pose3d> estimateVisionScoreCubePose();

    Optional<Pose3d> estimateVisionScoreRightConePose();

    Optional<Pose3d> estimateVisionLoadLeftPose();

    Optional<Pose3d> estimateVisionLoadRightPose();

    Optional<Pose3d> estimateVisionScoreLeftConePose();

    Optional<Pose3d> estimateVisionRobotPose();

    boolean isTargTrue();

    int getTagID();
}

