package org.bitbuckets.drive.sds;

public interface ISteerController {
    double getReferenceAngle();

    void setReferenceAngle(double referenceAngleRadians);

    double getStateAngle();

    /**
     * Force the offset of the steer controller motor's internal encoder
     * used to reset the encoder to be in sync with the absolute encoder
     *
     * @param position
     */
    void forceOffset(double position);

    double getAbsoluteAngle();

}
