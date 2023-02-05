package org.bitbuckets.drive.controlsds.sds;

import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;

public interface ISwerveModule {

    SwerveModulePosition getPosition();
    SwerveModuleState getState();
    double getDriveVelocity();
    double getSteerAngle();

    void set(double driveVoltage, double steerAngle);
}
