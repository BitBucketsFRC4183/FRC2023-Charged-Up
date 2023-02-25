package org.bitbuckets.lib;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEvent;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardContainer;
import org.bitbuckets.lib.tune.IValueTuner;
import org.bitbuckets.lib.tune.ValueTuner;
import org.bitbuckets.lib.tune.EnumTuner;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Random;

public interface ITuneAs<T> {

    EnumSet<NetworkTableEvent.Kind> REMOTE = EnumSet.of(NetworkTableEvent.Kind.kValueRemote);
    IValueTuner<T> generate(String key, ShuffleboardContainer container, T data, IValueTuner<ProcessMode> self);

    ITuneAs<Double> DOUBLE_INPUT = (k,c,d,s) -> {

        var entry = c.add(k, d).getEntry();
        var tuneable = new ValueTuner<>(entry, s, d);
        NetworkTableInstance.getDefault().addListener(entry, REMOTE, tuneable);

        return tuneable;
    };



    static <E extends Enum<E>> ITuneAs<E> ENUM(Class<E> enumType) {
        return (k,c,d,s) -> {
            var e = c.add(".dbg", false).getEntry();
            NetworkTable hack = e.getTopic().getInstance().getTable(e.getTopic().getName().replaceAll("/.dbg", "/" + k));

            hack.getEntry(".controllable").setBoolean(true);
            hack.getEntry(".name").setString(k);
            hack.getEntry(".type").setString("String Chooser");
            hack.getEntry("default").setString(d.name());
            hack.getEntry("options").setStringArray(Arrays.stream(d.getDeclaringClass().getEnumConstants()).map(Enum::name).toArray(String[]::new));

            var selected = hack.getEntry("selected");
            var active = hack.getEntry("active");

            selected.setString(d.name());
            active.setString(d.name());

            var init = new EnumTuner<>(enumType, d, selected, active);

            NetworkTableInstance.getDefault().addListener(selected, REMOTE, init);

            return init;
        };
    }


    static <E extends Enum<E>> ITuneAs<E> SIDEBAR_ENUM(Class<E> enumType) {
        return (k,c,d,s) -> {
            var e = c.add(".dbg-" + Math.random() * 90, false).getEntry();
            NetworkTable hack = e.getTopic().getInstance().getTable(e.getTopic().getName().replaceAll("/.dbg", "/" + k));

            hack.getEntry(".controllable").setBoolean(true);
            hack.getEntry(".name").setString(k);
            hack.getEntry(".type").setString("String Chooser");
            hack.getEntry("default").setString(d.name());
            hack.getEntry("options").setStringArray(Arrays.stream(d.getDeclaringClass().getEnumConstants()).map(Enum::name).toArray(String[]::new));

            var selected = hack.getEntry("selected");
            var active = hack.getEntry("active");

            selected.setString(d.name());
            active.setString(d.name());

            var init = new EnumTuner<>(enumType, d, selected, active);

            NetworkTableInstance.getDefault().addListener(selected, REMOTE, init);

            return init;
        };
    }





}
