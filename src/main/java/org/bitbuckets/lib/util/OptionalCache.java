package org.bitbuckets.lib.util;

import java.util.Optional;

public class OptionalCache<T> {

    final int cacheRepeatCount;

    public OptionalCache(int cacheRepeatCount) {
        this.cacheRepeatCount = cacheRepeatCount;
    }

    T cachedVariant; //nullable
    int cachedCount = 0;

    public Optional<T> orCache(Optional<T> data) {
        if (data.isPresent()) {
            cachedVariant = data.get();
            cachedCount = 0;
            return Optional.of(cachedVariant);
        } else {
            if (cachedCount < cacheRepeatCount) {
                cachedCount++;
                return Optional.ofNullable(cachedVariant); //dont take cahnces
            } else {
                cachedCount = 0;
                cachedVariant = null;
                return Optional.empty();
            }
        }
    }

}
