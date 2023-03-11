package org.bitbuckets.arm;

public enum ArmFSM {

    IDLE,

    MANUAL,
    STOW,

    PREPARE,
    UNSTOW,
    ACTUATE_GRIPPER, //UNUSED
    LOAD,


    SCORE_MID,
    SCORE_HIGH,
    SCORE_LOW,

    HUMAN_INTAKE,
    GROUND_INTAKE,

    DEBUG_TO_DEGREES,
    CUBE,
    CONE

}
