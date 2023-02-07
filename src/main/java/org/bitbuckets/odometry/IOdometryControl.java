package org.bitbuckets.odometry;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;

public interface IOdometryControl {

    Pose2d estimatePose2d();
    Rotation2d getRotation2d();

    double getYaw_deg();
    double getRoll_deg();
    void zero();


}
