package org.bitbuckets.lib;

/**
 * Represents the setup phase for some code that will run during the periodic loop. Need to provide a root
 * level version of this to the loggable robot
 * <p>
 * <p>
 * UPDATED
 * Represents the initilization of any outside factors of <T> that <T> needs to run
 * like loops, etc, etc
 *
 * @param <T> type of object it will produce
 */
public interface ISetup<T> {

    /**
     * function representing the setup of a piece of code that requires robot-initalized-specific devices
     * <p>
     * make sure you call any child factory with tools.child() and NOT with this tools instance
     *
     * @param self a variety of tools.
     * @return a fully initialized object
     */
    T build(IProcess self);


}
