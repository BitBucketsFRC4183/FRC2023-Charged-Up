package org.bitbuckets.lib.hardware;

/**
 * How the CAN frames should be optimized
 */
public enum OptimizationMode {

    GENERIC,
    VOLTAGE,
    OFFBOARD_VEL_PID,
    OFFBOARD_POS_PID,
    LQR


}
