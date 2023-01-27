package org.bitbuckets.drive.controlsds.sds;

public interface ISteerController {
    double getReferenceAngle();

    void setReferenceAngle(double referenceAngleRadians);

    double getStateAngle();
}
