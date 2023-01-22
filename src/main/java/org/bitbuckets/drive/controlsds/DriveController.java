package org.bitbuckets.drive.controlsds;

public interface DriveController {
    void setReferenceVoltage(double voltage);

    double getStateVelocity();
}
