package org.bitbuckets.drive;

public enum DriveFSM {

    UNINITIALIZED,
    AUTO_NORMAL,
    TELEOP_NORMAL,
    TELEOP_APPROACHING_CS,
    TELEOP_BALANCING,
    TELEOP_BALANCE_ENGAGED,
    TELEOP_AUTOHEADING,
    AUTO_PATHFINDING,
    AUTO_BALANCING,
    DRIVE_NORMAL


}
