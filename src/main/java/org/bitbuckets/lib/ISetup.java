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
     * never call this
     */
    T build(IProcess self);



}
