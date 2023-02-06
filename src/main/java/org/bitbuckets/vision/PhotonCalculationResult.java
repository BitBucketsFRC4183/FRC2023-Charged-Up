package org.bitbuckets.vision;

import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;

public class PhotonCalculationResult {
    public final Pose3d robotPose;
    public final Pose3d goalPose;
    public final Translation2d translationToTag;
    public final Rotation2d targetYaw;
    public final double yaw;

    public PhotonCalculationResult(Pose3d robotPose, Pose3d goalPose, Translation2d translationToTag, Rotation2d targetYaw, double yaw) {
        this.robotPose = robotPose;
        this.goalPose = goalPose;
        this.translationToTag = translationToTag;
        this.targetYaw = targetYaw;
        this.yaw = yaw;
    }

    @Override
    public String toString() {
        return "PhotonCalculationResult{" +
                "robotPose=" + robotPose +
                ", goalPose=" + goalPose +
                ", translationToTag=" + translationToTag +
                ", targetYaw=" + targetYaw +
                ", yaw=" + yaw +
                '}';
    }
}
