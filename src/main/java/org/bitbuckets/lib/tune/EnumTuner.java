package org.bitbuckets.lib.tune;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableEvent;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class EnumTuner<T extends Enum<T>> implements IForceSendTuner<T>, Consumer<NetworkTableEvent> {

    final Class<T> enumType;
    final AtomicReference<AtomicRecord<T>> cachedValue;
    final NetworkTableEntry entry;
    final Consumer<NetworkTableEvent> passTo;

    static {
        SendableChooser<String> ass = new SendableChooser<>();
        ass.setDefaultOption("anal", "oo");
        ass.addOption("butt", "butt");
        SmartDashboard.putData(ass);

    }

    int i = 0;

    public EnumTuner(NetworkTable subtable, Class<T> enumType, T defaultValue, Consumer<NetworkTableEvent> passTo) {
        this.passTo = passTo;
        //setup shuffleboard support
        subtable.getEntry(".controllable").setBoolean(true);
        subtable.getEntry(".name").setString("mode " + ++i);
        subtable.getEntry(".type").setString("String Chooser");
        subtable.getEntry(".instance").setInteger(0);
        subtable.getEntry("default").setString((defaultValue).name());

        List<String> toBuild = new ArrayList<>();
        for (T enumInstance : enumType.getEnumConstants()) {
            toBuild.add((enumInstance).name());
        }

        subtable.getEntry("options").setStringArray(toBuild.toArray(String[]::new));
        this.entry = subtable.getEntry("selected");
        this.entry.setString("no");
        subtable.getEntry("active").setString("no");

        this.enumType = enumType;
        this.cachedValue = new AtomicReference<>(new AtomicRecord<T>(defaultValue, false));

        //entry.getInstance().addListener(entry, EnumSet.of(NetworkTableEvent.Kind.kValueRemote), this);
        //entry.getInstance().addListener(entry, EnumSet.of(NetworkTableEvent.Kind.kValueRemote), passTo);
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

    @Override
    public void forceToValue(T value) {
        this.cachedValue.set(new AtomicRecord<>(value, true));
        entry.setString(value.name());
    }
}
