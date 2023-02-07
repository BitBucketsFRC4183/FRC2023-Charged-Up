package org.bitbuckets.lib.tune.type;

import edu.wpi.first.networktables.NetworkTableEvent;
import org.bitbuckets.lib.tune.IValueTuner;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class EnumTuner<T extends Enum<T>> implements IValueTuner<T>, Consumer<NetworkTableEvent> {

    final Class<T> enumType;
    final AtomicReference<AtomicRecord<T>> cachedValue;

    public EnumTuner(Class<T> enumType, T defaultValue) {
        this.enumType = enumType;
        this.cachedValue = new AtomicReference<>(new AtomicRecord<T>(defaultValue, false));
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
    public boolean hasUpdated() {
        return cachedValue.get().hasUpdated; //
    }

    @Override
    public void accept(NetworkTableEvent networkTableEvent) {

        String str = networkTableEvent.valueData.value.getString();
        T cached = Enum.valueOf(enumType, str);

        cachedValue.set(new AtomicRecord<>(cached, true));
    }
}
