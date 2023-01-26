package org.bitbuckets.drive.controlsds.sds;

public interface DriveController {
    void setReferenceVoltage(double voltage);

    double getStateVelocity();
}
