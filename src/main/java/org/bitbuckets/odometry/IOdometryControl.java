package org.bitbuckets.odometry;

import edu.wpi.first.math.geometry.Pose2d;
import org.bitbuckets.lib.hardware.IGyro;

/**
 * Extends IGyro and offers pose estimation
 */
public interface IOdometryControl {


    Pose2d estimatePose_trueFieldPose();

    IGyro getGyro();

    void zeroOdo();
    void zeroGyro();
    void setPos(Pose2d pose_trueFieldRelative);
}
