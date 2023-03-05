package org.bitbuckets.vision;

import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;

public class PhotonCalculationResult {
    public final Pose3d robotPose;
    public final Pose3d cubeScorePose;

    public final Pose3d coneLeftScorePose;
    public final Pose3d coneRightScorePose;
    public final Pose3d loadLeftPose;
    public final Pose3d loadRightPose;
    public final Translation2d translationToTag;
    public final Rotation2d targetYaw;
    public final double yaw;
    public final boolean isTargetTrue;
    public PhotonCalculationResult(Pose3d robotPose, Pose3d goalPose, Pose3d coneLeftScorePose, Pose3d coneRightScorePose, Pose3d loadLeftPose, Pose3d loadRightPose, Translation2d translationToTag, Rotation2d targetYaw, double yaw, boolean isTargetTrue) {
        this.robotPose = robotPose;
        this.cubeScorePose = goalPose;
        this.coneLeftScorePose = coneLeftScorePose;
        this.coneRightScorePose = coneRightScorePose;
        this.loadLeftPose = loadLeftPose;
        this.loadRightPose = loadRightPose;
        this.translationToTag = translationToTag;
        this.targetYaw = targetYaw;
        this.yaw = yaw;
        this.isTargetTrue = isTargetTrue;
    }

    @Override
    public String toString() {
        return "PhotonCalculationResult{" +
                "robotPose=" + robotPose +
                ", goalPose=" + cubeScorePose +
                ", translationToTag=" + translationToTag +
                ", targetYaw=" + targetYaw +
                ", yaw=" + yaw +
                ", is-there-a-target" + isTargetTrue +
                '}';
    }

}
