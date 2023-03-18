package org.bitbuckets.drive;

import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveModulePosition;

public interface IDriveControl {

    SwerveModulePosition[] currentPositions_initializationRelative();

    /**
     *
     * @param speeds_robotRelative Speeds relative to the robot i.e. current facing
     */
    void drive(ChassisSpeeds speeds_robotRelative);
    void stop();

    double getMaxVelocity();
    double getMaxAngularVelocity();

}
