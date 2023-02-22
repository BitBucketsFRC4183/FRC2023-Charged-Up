package org.bitbuckets.lib.tune;

import edu.wpi.first.networktables.GenericEntry;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableEvent;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

//TODO this doesn't follow the Tuner contract and also has the potential to be really laggy
public class SuperiorEnumTuner<T extends Enum<T>> implements IForceSendTuner<T>, Consumer<NetworkTableEvent> {

    final Class<T> defaultType;
    final T defaultValue;
    final NetworkTableEntry selected;
    final NetworkTableEntry active;
    final AtomicReference<AtomicRecord<T>> activeInternal;

    public SuperiorEnumTuner(Class<T> defaultType, T defaultValue, NetworkTableEntry selected, NetworkTableEntry active) {
        this.defaultType = defaultType;
        this.defaultValue = defaultValue;
        this.selected = selected;
        this.active = active;
        this.activeInternal = new AtomicReference<>(new AtomicRecord<>(defaultValue,false));
    }

    @Override
    public void forceToValue(T value) {
        active.setString(value.name());
        selected.setString(value.name());

        activeInternal.set(new AtomicRecord<>(value, true));
    }

    @Override
    public T readValue() {


        return activeInternal.get().cachedPointer;
    }

    @Override
    public T consumeValue() {
        AtomicRecord<T> newVal = activeInternal.updateAndGet(record -> {
            if (record.hasUpdated) {
                return new AtomicRecord<>(record.cachedPointer, false);
            }

            return record; //avoid CAS operation to save loops
        });

        return newVal.cachedPointer;
    }

    @Override
    public boolean hasUpdated() {
        return false;
    }

    @Override
    public void accept(NetworkTableEvent networkTableEvent) {
        T enm = Enum.valueOf(defaultType, networkTableEvent.valueData.value.getString());
        active.setString(enm.name());
        activeInternal.set(new AtomicRecord<>(enm, true));
    }
}
