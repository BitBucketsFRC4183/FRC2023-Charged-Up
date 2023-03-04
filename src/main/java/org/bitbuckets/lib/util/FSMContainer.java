package org.bitbuckets.lib.util;

public interface FSMContainer<T> {

    void pushNext(T value);
    T getCurrent();
    T getLast();

}
