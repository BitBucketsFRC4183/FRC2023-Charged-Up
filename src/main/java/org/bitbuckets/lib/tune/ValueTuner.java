package org.bitbuckets.lib.tune;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEvent;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

/**
 * A tuneable value.
 * @param <T>
 */
public class ValueTuner<T> implements NetworkTable.TableEventListener, IValueTuner<T> {

    final T defaultValue;


    final AtomicReference<AtomicRecord> cachedValue;

    public ValueTuner(T firstValue) {
        this.defaultValue = firstValue;
        this.cachedValue = new AtomicReference<>(new AtomicRecord(firstValue, false));
    }


    @Override
    public T readValue() {
        return cachedValue.get().cachedPointer;
    }

    @Override
    public T consumeValue() {
        AtomicRecord nowStale = cachedValue.getAndUpdate(record -> {
            if (record.hasUpdated) {
                return new AtomicRecord(record.cachedPointer, false);
            }

            return record; //avoid CAS operation to save loops
        });

        return nowStale.cachedPointer;
    }

    @Override
    public void accept(NetworkTable table, String key, NetworkTableEvent event) {
        Object newObject = event.valueData.value.getValue();

        if (defaultValue.getClass().isEnum()) {

            Enum aaaa = (Enum) defaultValue;
            Enum coerced = Enum.valueOf(aaaa.getClass(), (String)newObject);

            cachedValue.set(new AtomicRecord((T) coerced, true));
        } else {
            cachedValue.set(new AtomicRecord((T) newObject, true));
        }

    }

    class AtomicRecord {
        final T cachedPointer;
        final boolean hasUpdated;

        public AtomicRecord(T cachedPointer, boolean hasUpdated) {
            this.cachedPointer = cachedPointer;
            this.hasUpdated = hasUpdated;
        }
    }



    public boolean hasUpdated() {
        return !cachedValue.get().hasUpdated; //
    }





}