package org.bitbuckets.odometry;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.SwerveModulePosition;

/**
 * Extends IGyro and offers pose estimation
 */
public interface IOdometryControl {


    Pose2d estimateFusedPose2d();

    Rotation2d getRotation2d();

    double getYaw_deg();
    double getPitch_deg();
    double getRoll_deg();

    void zero();
    void setPos(Rotation2d gyroAngle, Pose2d poseMeters);
}
