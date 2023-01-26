package org.bitbuckets.lib.tune;

/**
 * priority: high priority
 * TODO: document this
 * @param <T>
 */
public interface IValueTuner<T> {

    /**
     * This will
     * @return
     */
    T readValue();
    T consumeValue();

    boolean hasUpdated();

}
