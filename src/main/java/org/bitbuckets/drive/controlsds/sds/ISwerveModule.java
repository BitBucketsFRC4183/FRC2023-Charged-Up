package org.bitbuckets.drive.controlsds.sds;

public interface ISwerveModule {

    double getDrivePosition_meters();
    double getDriveVelocity();

    double getSteerAngle();

    void set(double driveVoltage, double steerAngle);
}
