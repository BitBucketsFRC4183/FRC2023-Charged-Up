package org.bitbuckets.lib;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEvent;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardContainer;
import org.bitbuckets.lib.tune.IValueTuner;
import org.bitbuckets.lib.tune.ModernTuner;
import org.bitbuckets.lib.tune.SuperiorEnumTuner;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.function.Consumer;

public interface ITuneAs<T> {

    EnumSet<NetworkTableEvent.Kind> REMOTE = EnumSet.of(NetworkTableEvent.Kind.kValueRemote);
    IValueTuner<T> generate(String key, ShuffleboardContainer container, T data, IValueTuner<ProcessMode> self, Consumer<NetworkTableEvent> delegate);

    ITuneAs<Double> DOUBLE_INPUT = (k,c,d,s,dg) -> {

        var entry = c.add(k, d).getEntry();
        var tuneable = new ModernTuner<>(entry, s, d);
        NetworkTableInstance.getDefault().addListener(entry, REMOTE, tuneable);
        if (dg != null) {
            NetworkTableInstance.getDefault().addListener(entry, REMOTE, dg);
        }

        return tuneable;
    };


    static <E extends Enum<E>> ITuneAs<E> ENUM_INPUT(Class<E> enumType) {
        return (k,c,d,s,dg) -> {
            var e = c.add(".dbg", false).getEntry();
            NetworkTable hack = e.getTopic().getInstance().getTable(e.getTopic().getName().replaceAll("/.dbg", "/" + k));

            hack.getEntry(".controllable").setBoolean(true);
            hack.getEntry(".name").setString("Mode Changer");
            hack.getEntry(".type").setString("String Chooser");
            hack.getEntry("default").setString(d.name());
            hack.getEntry("options").setStringArray(Arrays.stream(d.getDeclaringClass().getEnumConstants()).map(Enum::name).toArray(String[]::new));

            var selected = hack.getEntry("selected");
            var active = hack.getEntry("active");

            selected.setString(d.name());
            active.setString(d.name());

            var init = new SuperiorEnumTuner<>(enumType, d, selected, active);

            NetworkTableInstance.getDefault().addListener(selected, REMOTE, init);
            if (dg != null) {
                NetworkTableInstance.getDefault().addListener(selected, REMOTE, dg);
            }

            return init;
        };
    }





}
