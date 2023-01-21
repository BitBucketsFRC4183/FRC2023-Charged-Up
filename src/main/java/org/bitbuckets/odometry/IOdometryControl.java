package org.bitbuckets.odometry;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Pose3d;

public interface IOdometryControl {


    /**
     * Should estimate where the robot is in 2d space of the field
     * @return the pose
     */
    Pose2d estimateRobotPose();

    Pose3d estimateRobotPose3d();



}
