package org.bitbuckets.lib.tune;

public interface IForceSendTuner<T> extends IValueTuner<T> {

    @Deprecated //bad bad bad bad
    void forceToValue(T value);

}
