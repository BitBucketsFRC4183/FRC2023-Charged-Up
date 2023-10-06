package org.bitbuckets.drive.sds;

public interface IDriveController {
    void setReferenceVoltage(double voltage);

    double getStateVelocity();
    double getStatePosition_meters();
}
