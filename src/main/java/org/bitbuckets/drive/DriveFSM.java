package org.bitbuckets.drive;

public enum DriveFSM {

    UNINITIALIZED,
    TELEOP_NORMAL,
    TELEOP_EXTERNAL_CONTROL, //Another subsystem is using the drivebase


    //remove these
    TELEOP_APPROACHING_CS,
    TELEOP_BALANCING,
    TELEOP_BALANCE_ENGAGED,
    TELEOP_AUTOHEADING,
    TELEOP_VISION,

    AUTO_PATHFINDING,
    AUTO_BALANCING


}
