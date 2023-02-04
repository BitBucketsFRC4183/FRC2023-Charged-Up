package org.bitbuckets.lib.tune.type;

public class AtomicRecord<T> {

    final T cachedPointer;
    final boolean hasUpdated;

    public AtomicRecord(T cachedPointer, boolean hasUpdated) {
        this.cachedPointer = cachedPointer;
        this.hasUpdated = hasUpdated;
    }

}
