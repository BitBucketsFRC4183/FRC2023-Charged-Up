package org.bitbuckets.drive.controlsds.sds;

public interface IDriveController {
    void setReferenceVoltage(double voltage);

    double getStateVelocity();
}
