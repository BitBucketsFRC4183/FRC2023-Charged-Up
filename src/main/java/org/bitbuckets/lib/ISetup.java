package org.bitbuckets.lib;

/**
 * Represents the setup phase for some code that will run during the periodic loop. Need to provide a root
 * level version of this to the loggable robot
 *
 *
 * UPDATED
 * Represents the initilization of any outside factors of <T> that <T> needs to run
 * like loops, etc, etc
 * @param <T> type of object it will produce
 */
public interface ISetup<T> {

    /**
     * function representing the setup of a piece of code that requires robot-initalized-specific devices
     *
     * make sure you call any child factory with tools.child() and NOT with this tools instance
     * @param path a variety of tools.
     * @return a fully initialized object
     */
    T build(ProcessPath path);


    /**
     * TODO make this default behavior because sometimes you want the same object in multiple places (dumb)
     * @param delegate
     * @return
     * @param <T>
     */
    static <T> ISetup<T> caching(ISetup<T> delegate) {
        return new ISetup<>() {

            T cached = null;

            @Override
            public T build(ProcessPath path) {
                if (cached != null) {
                    return cached;
                }

                return cached = delegate.build(path);
            }
        };
    }

}
