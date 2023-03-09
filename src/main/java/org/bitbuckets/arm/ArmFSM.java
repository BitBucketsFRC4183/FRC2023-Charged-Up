package org.bitbuckets.arm;

public enum ArmFSM {

    IDLE,

    MANUAL,
    STORAGE,

    PREPARE,

    UNSTOW,

    SCORE_MID,
    SCORE_HIGH,
    SCORE_LOW,

    HUMAN_INTAKE,
    GROUND_INTAKE,

    DEBUG_TO_DEGREES,


    GRIPPER_OPEN,
    GRIPPER_CLOSE_CONE,
    GRIPPER_CLOSE_CUBE


}
