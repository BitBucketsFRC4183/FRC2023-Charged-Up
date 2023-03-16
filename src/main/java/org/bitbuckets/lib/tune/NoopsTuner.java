package org.bitbuckets.lib.tune;

import java.util.function.Consumer;

public class NoopsTuner<T> implements IValueTuner<T> {

    final T value;

    public NoopsTuner(T value) {
        this.value = value;
    }

    @Override
    public T readValue() {
        return value;
    }

    @Override
    public T consumeValue() {
        return value;
    }

    @Override
    public boolean hasUpdated() {
        return false;
    }


    @Override
    public void bind(Consumer<T> data) {
        //do nothing
    }
}
