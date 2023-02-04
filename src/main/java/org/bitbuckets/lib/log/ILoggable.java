package org.bitbuckets.lib.log;

/**
 * Something that can be logged
 * @param <T>
 */
public interface ILoggable<T> {

    /**
     * Send the logged data to the dashboard AND log it to a file, or other behavior
     * @param data your data
     */
    void log(T data);

}
