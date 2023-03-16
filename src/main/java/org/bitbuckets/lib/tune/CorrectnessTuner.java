package org.bitbuckets.lib.tune;

import edu.wpi.first.networktables.NetworkTableEntry;

public class CorrectnessTuner<T> implements IValueTuner<T>{

    final NetworkTableEntry entry;

    public CorrectnessTuner(NetworkTableEntry entry) {
        this.entry = entry;
    }

    @Override
    public T readValue() {
        return (T) entry.getValue().getValue();
    }

    @Override
    public T consumeValue() {
        return (T) entry.getValue().getValue();
    }

    @Override
    public boolean hasUpdated() {
        return false;
    }


}
