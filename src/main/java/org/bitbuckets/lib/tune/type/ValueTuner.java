package org.bitbuckets.lib.tune.type;

import edu.wpi.first.networktables.NetworkTableEvent;
import org.bitbuckets.lib.tune.IValueTuner;
import org.bitbuckets.lib.tune.type.AtomicRecord;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

/**
 * A tuneable value.
 *
 * @param <T>
 */
@Deprecated
public class ValueTuner<T> implements Consumer<NetworkTableEvent>, IValueTuner<T> {

    final T defaultValue;


    final AtomicReference<AtomicRecord<T>> cachedValue;

    public ValueTuner(T firstValue) {
        this.defaultValue = firstValue;
        this.cachedValue = new AtomicReference<>(new AtomicRecord<>(firstValue, false));
    }


    @Override
    public T readValue() {
        return cachedValue.get().cachedPointer;
    }

    @Override
    public T consumeValue() {
        AtomicRecord<T> nowStale = cachedValue.getAndUpdate(record -> {
            if (record.hasUpdated) {
                return new AtomicRecord<>(record.cachedPointer, false);
            }

            return record; //avoid CAS operation to save loops
        });

        return nowStale.cachedPointer;
    }


    @Override
    public void accept(NetworkTableEvent networkTableEvent) {
        Object newObject = networkTableEvent.valueData.value.getValue();

        cachedValue.set(new AtomicRecord<>((T) newObject, true));
    }



    public boolean hasUpdated() {
        return cachedValue.get().hasUpdated; //
    }


}
