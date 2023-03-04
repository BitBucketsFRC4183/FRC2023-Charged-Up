package org.bitbuckets.lib.tune;

import java.util.function.Consumer;

/**
 * Represents a value or values that can be tuned from the smart dashboard
 * @param <T> the data type
 */
public interface IValueTuner<T> {

    /**
     * This will read the data without marking it as stale
     * @return the data that is most up to date
     */
    T readValue();

    /**
     * This will read the latest data but mark it as stale.
     * The only effect this has is that hasUpdated will then return false until new data arrives
     * @return data
     */
    T consumeValue();

    /**
     *
     * @return whether the data has updated since it was last consumed from this tuner
     */
    boolean hasUpdated();

    void bind(Consumer<T> data);

}
