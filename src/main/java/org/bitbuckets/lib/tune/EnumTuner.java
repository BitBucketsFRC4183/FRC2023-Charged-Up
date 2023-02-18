package org.bitbuckets.lib.tune;

import edu.wpi.first.networktables.*;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardContainer;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;



//oops
public class EnumTuner<T extends Enum<T>> implements IForceSendTuner<T>, Runnable{

    final Class<T> enumType;
    final GenericEntry entry;
    final Consumer<NetworkTableValue> passTo;

    public EnumTuner(ShuffleboardContainer subtable, Class<T> enumType, T defaultValue, Consumer<NetworkTableValue> passTo) {
        this.passTo = passTo;
        this.enumType = enumType;


        SendableChooser<T> chooser = new SendableChooser<>();
        chooser.setDefaultOption(enumType.getName(), defaultValue);



        List<String> toBuild = new ArrayList<>();
        for (T enumInstance : enumType.getEnumConstants()) {
            chooser.addOption(enumInstance.name(), enumInstance);
        }

        this.entry = subtable.add("selected", defaultValue.name()).getEntry();
        subtable.add("options", toBuild.toArray(String[]::new));
        subtable.addString("active", this::readName);




        NetworkTableInstance.getDefault().addListener(entry, EnumSet.of(NetworkTableEvent.Kind.kValueRemote), this);
        NetworkTableInstance.getDefault().addListener(entry, EnumSet.of(NetworkTableEvent.Kind.kValueRemote), passTo);
    }

    @Override
    public T readValue() {
        return cachedValue.get().cachedPointer;
    }

    String readName() {
        return cachedValue.get().cachedPointer.name();
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

    @Override
    public void run() {
        if (entry.readQueue().length > 0) {
            //updated
            passTo.accept(entry.get());
        }
    }
}
