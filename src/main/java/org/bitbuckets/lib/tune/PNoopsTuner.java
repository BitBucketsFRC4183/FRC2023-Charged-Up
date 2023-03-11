package org.bitbuckets.lib.tune;

import org.bitbuckets.lib.ProcessMode;

import java.util.function.Consumer;

public class PNoopsTuner implements IForceSendTuner<ProcessMode> {
    //todo make a root tuner

    @Override
    public void forceToValue(ProcessMode value) {

    }

    @Override
    public ProcessMode readValue() {
        return ProcessMode.LOG_COMPETITION;
    }

    @Override
    public ProcessMode consumeValue() {
        return ProcessMode.LOG_COMPETITION;
    }

    @Override
    public boolean hasUpdated() {
        return false;
    }

    @Override
    public void bind(Consumer<ProcessMode> data) {

    }
}

