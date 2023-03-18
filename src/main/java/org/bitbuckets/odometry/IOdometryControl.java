package org.bitbuckets.odometry;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import org.bitbuckets.lib.hardware.IGyro;

/**
 * Extends IGyro and offers pose estimation
 */
public interface IOdometryControl {


    Pose2d estimatePose_trueFieldPose();

    IGyro getGyro();

    void zero();
    void setPos(Pose2d pose_trueFieldRelative);
}
