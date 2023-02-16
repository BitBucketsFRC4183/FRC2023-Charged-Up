package org.bitbuckets.lib.tune;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableEvent;
import org.bitbuckets.lib.ProcessMode;

import java.util.EnumSet;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

/**
 * This tuner will simply return default data if not in tuning mode
 * @param <T>
 */
public class ModernTuner<T> implements IValueTuner<T>, Consumer<NetworkTableEvent>, Runnable {

    final NetworkTableEntry entry;
    final IValueTuner<ProcessMode> processMode;
    final T defaultData;
    final AtomicReference<AtomicRecord<T>> cachedValue;

    public ModernTuner(NetworkTableEntry entry, IValueTuner<ProcessMode> processMode, T defaultData) {
        this.entry = entry;
        this.processMode = processMode;
        this.defaultData = defaultData;
        this.cachedValue = new AtomicReference<>(new AtomicRecord<>(defaultData, false));

        entry.getInstance().addListener(entry, EnumSet.of(NetworkTableEvent.Kind.kValueRemote), this);
    }

    @Override
    public T readValue() {
        if (processMode.readValue().level > ProcessMode.TUNE.level) {
            return defaultData;
        }

        return cachedValue.get().cachedPointer;
    }

    @Override
    public T consumeValue() {
        if (processMode.readValue().level > ProcessMode.TUNE.level) {
            return defaultData;
        }

        AtomicRecord<T> nowStale = cachedValue.getAndUpdate(record -> {
            if (record.hasUpdated) {
                return new AtomicRecord<>(record.cachedPointer, false);
            }

            return record; //avoid CAS operation to save loops
        });

        return nowStale.cachedPointer;
    }

    @Override
    public boolean hasUpdated() {
        if (processMode.readValue().level > ProcessMode.TUNE.level) {
            return false;
        }

        return cachedValue.get().hasUpdated;
    }

    @Override
    public void accept(NetworkTableEvent networkTableEvent) {
        if (processMode.readValue().level > ProcessMode.TUNE.level) return;

        Object newObject = networkTableEvent.valueData.value.getValue();

        cachedValue.set(new AtomicRecord<>((T) newObject, true));
    }

    @Override
    public void run() {
        if (processMode.readValue().level > ProcessMode.TUNE.level) {
            entry.setValue(defaultData);
        }
    }
}
