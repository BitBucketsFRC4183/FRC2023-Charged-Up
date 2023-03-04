package org.bitbuckets.lib;

/**
 * Represents something with position control capabilities, typically should be tuneable
 */
public interface IPositionControl {

    void moveToPositionSetpoint(double setpoint_mechanismRot);

    double getPositionError();

}
