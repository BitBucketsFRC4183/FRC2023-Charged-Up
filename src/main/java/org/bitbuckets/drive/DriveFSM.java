package org.bitbuckets.drive;

public enum DriveFSM {

    UNINITIALIZED,
    TELEOP_NORMAL,
    TELEOP_APPROACHING_CS,
    TELEOP_BALANCING,
    TELEOP_BALANCE_ENGAGED,
    TELEOP_AUTOHEADING,

    PID_TUNING,

    PID_TUNING1


}
