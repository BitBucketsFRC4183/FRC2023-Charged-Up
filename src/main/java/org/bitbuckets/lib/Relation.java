package org.bitbuckets.lib;

/**
 * Describes something's relation to its parent
 *
 * I.e. If you put an IPidController as a child of a IMotorController, it is a software component of a hardware component
 */
public enum Relation {

    SOFTWARE_COMPONENT,
    HARDWARE_COMPONENT,
    GENERIC_COMPONENT,
}
