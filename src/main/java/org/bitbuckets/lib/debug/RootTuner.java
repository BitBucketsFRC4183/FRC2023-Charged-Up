package org.bitbuckets.lib.debug;

import org.bitbuckets.lib.ProcessMode;
import org.bitbuckets.lib.tune.IForceSendTuner;

import java.util.function.Consumer;

public class RootTuner implements IForceSendTuner<ProcessMode> {

    IForceSendTuner<ProcessMode> readyDelegate;

    @Override
    public void forceToValue(ProcessMode value) {
        if (readyDelegate != null) {
            readyDelegate.forceToValue(value);
        }
    }

    @Override
    public ProcessMode readValue() {
        if (readyDelegate != null) {
            return readyDelegate.readValue();
        }

        return ProcessMode.LOG_COMPETITION;
    }

    @Override
    public ProcessMode consumeValue() {
        if (readyDelegate != null) {
            return readyDelegate.consumeValue();
        }

        return ProcessMode.LOG_COMPETITION;
    }

    @Override
    public boolean hasUpdated() {
        if (readyDelegate != null) {
            return readyDelegate.hasUpdated();
        }

        return false;
    }



    @Override
    public void bind(Consumer<ProcessMode> data) {

    }
}
