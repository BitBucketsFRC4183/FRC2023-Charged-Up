package org.bitbuckets.lib.core;

/**
 * Standard WPI style lifecycle functions
 */
@AutoTrait
public interface HasLifecycle {

    default void autonomousInit() {
    }

    default void autonomousPeriodic() {
    }

    default void teleopInit() {
    }

    default void teleopPeriodic() {
    }


    default void disabledInit() {
    }

    default void disabledPeriodic() {
    }
}
