package org.bitbuckets.drive;

import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.wpilibj2.command.Subsystem;

public interface IDriveControl extends Subsystem {

    SwerveModulePosition[] currentPositions();
    void drive(ChassisSpeeds speeds);
    void stop();

    double getMaxVelocity();
    double getMaxAngularVelocity();

}
