package org.bitbuckets.lib.tune;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;

public class ChooserTuner<T> implements IValueTuner<T> {

    final SendableChooser<T> sendableChooser;

    public ChooserTuner(SendableChooser<T> sendableChooser) {
        this.sendableChooser = sendableChooser;
    }

    @Override
    public T readValue() {
        return sendableChooser.getSelected();
    }

    @Override
    public T consumeValue() {
        return sendableChooser.getSelected();
    }

    @Override
    public boolean hasUpdated() {
        return false;
    }
}
