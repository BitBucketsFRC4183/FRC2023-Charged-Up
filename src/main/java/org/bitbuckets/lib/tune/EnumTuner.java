package org.bitbuckets.lib.tune;

import edu.wpi.first.networktables.*;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardContainer;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;



//oops
public class EnumTuner<T extends Enum<T>> implements IForceSendTuner<T>, Runnable{

    final Class<T> enumType;
    final SendableChooser<T> chooser;
    final Consumer<T> consumer;

    static {
        Shuffleboard.getTab("l")
                .add("l", "l")
                .withWidget(BuiltInWidgets.kComboBoxChooser)
                .withProperties(Map.of("active", "l"));

        Shuffleboard.getTab("n")
                .add(new SendableChooser<>());
    }

    public EnumTuner(ShuffleboardContainer subtable, Class<T> enumType, T defaultValue, Consumer<T> passTo) {
        this.enumType = enumType;
        this.chooser = new SendableChooser<>();
        this.consumer = passTo;




        chooser.setDefaultOption(enumType.getName(), defaultValue);
        for (T enumInstance : enumType.getEnumConstants()) {
            chooser.addOption(enumInstance.name(), enumInstance);
        }
    }

    @Override
    public T readValue() {
        return chooser.getSelected();
    }


    @Override
    public T consumeValue() {
        return chooser.getSelected();
    }

    @Override
    public boolean hasUpdated() {
        return false;
    }

    @Override
    public void forceToValue(T value) {

    }

    T last = null;

    @Override
    public void run() {
        T next = chooser.getSelected();

        if (next != last) {
            consumer.accept(next);
        }

        last = next;
    }
}
