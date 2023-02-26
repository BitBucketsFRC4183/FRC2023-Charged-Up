package org.bitbuckets.lib.tune;

import org.bitbuckets.lib.ProcessMode;

import java.util.function.Consumer;

public class NoopsTuner<T> implements IForceSendTuner<T> {
    //todo make a root tuner

    final T data;

    public NoopsTuner(T data) {
        this.data = data;
    }

    @Override
    public void forceToValue(T value) {

    }

    @Override
    public T readValue() {
        return data;
    }

    @Override
    public T consumeValue() {
        return data;
    }

    @Override
    public boolean hasUpdated() {
        return false;
    }

    @Override
    public void bind(Consumer<T> data) {

    }
}

