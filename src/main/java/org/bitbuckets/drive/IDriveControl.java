package org.bitbuckets.drive;

import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveModulePosition;

public interface IDriveControl {

    SwerveModulePosition[] currentPositions();
    void drive(ChassisSpeeds speeds);
    void stop();

    void stopGentle();

    void stopSticky();

    double getMaxVelocity();
    double getMaxAngularVelocity();

}
