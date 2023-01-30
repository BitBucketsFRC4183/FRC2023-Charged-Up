package org.bitbuckets.drive.controlsds.sds;

public interface ISwerveModule {
    double getDriveVelocity();

    double getSteerAngle();

    void set(double driveVoltage, double steerAngle);
}
