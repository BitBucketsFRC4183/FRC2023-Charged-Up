package org.bitbuckets.lib.tune;

import edu.wpi.first.networktables.GenericEntry;
import edu.wpi.first.networktables.NetworkTableEvent;
import org.bitbuckets.lib.ProcessMode;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

/**
 * This tuner will simply return default data if not in tuning mode
 * @param <T>
 */
public class ValueTuner<T> implements IValueTuner<T>, Consumer<NetworkTableEvent> {

    final GenericEntry entry;
    final IValueTuner<ProcessMode> processMode;
    final T defaultData;
    final AtomicReference<AtomicRecord<T>> cachedValue;

    final List<Consumer<T>> bound = new ArrayList<>();

    public ValueTuner(GenericEntry entry, IValueTuner<ProcessMode> processMode, T defaultData) {
        this.entry = entry;
        this.processMode = processMode;
        this.defaultData = defaultData;
        this.cachedValue = new AtomicReference<>(new AtomicRecord<>(defaultData, false));

        processMode.bind(this::onProcessModeChange);
    }

    void onProcessModeChange(ProcessMode mode) {
        if (mode != ProcessMode.TUNE) {
            entry.setValue(defaultData);
        }
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

        AtomicRecord<T> newVal = cachedValue.updateAndGet(record -> {
            if (record.hasUpdated) {
                return new AtomicRecord<>(record.cachedPointer, false);
            }

            return record; //avoid CAS operation to save loops
        });

        return newVal.cachedPointer;
    }

    @Override
    public boolean hasUpdated() {
        if (processMode.readValue().level > ProcessMode.TUNE.level) {
            return false;
        }

        return cachedValue.get().hasUpdated;
    }

    @Override
    public synchronized void bind(Consumer<T> data) {
        bound.add(data);
    }

    @Override
    public void accept(NetworkTableEvent networkTableEvent) {
        if (processMode.readValue().level > ProcessMode.TUNE.level) {
            entry.setValue(defaultData);
            return;
        }

        Object newObject = networkTableEvent.valueData.value.getValue();
        T object = (T) newObject;

        cachedValue.set(new AtomicRecord<>(object, true));
        runBound(object);

    }

    public synchronized void runBound(T newData) {
        for (Consumer<T> consumer : bound) {
            consumer.accept(newData);
        }
    }

}
