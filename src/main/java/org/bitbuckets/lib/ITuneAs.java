package org.bitbuckets.lib;

import edu.wpi.first.networktables.GenericEntry;
import edu.wpi.first.networktables.NetworkTableEvent;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardContainer;
import org.bitbuckets.lib.tune.EnumTuner;
import org.bitbuckets.lib.tune.IValueTuner;
import org.bitbuckets.lib.tune.ModernTuner;

import java.util.EnumSet;
import java.util.function.Consumer;

public interface ITuneAs<T> {

    IValueTuner<T> generate(String key, ShuffleboardContainer container, T data, IValueTuner<ProcessMode> self, Consumer<NetworkTableEvent> delegate);

    ITuneAs<Double> DOUBLE_INPUT = (k,c,d,s,dg) -> {
        c.add("","_").close();

        var entry = c.add(k, d).getEntry();
        var tuneable = new ModernTuner<>(entry, s, d);
        NetworkTableInstance.getDefault().addListener(entry, EnumSet.of(NetworkTableEvent.Kind.kValueRemote), tuneable);
        if (dg != null) {
            NetworkTableInstance.getDefault().addListener(entry, EnumSet.of(NetworkTableEvent.Kind.kValueRemote), dg);
        }

        return tuneable;
    };


    //?????????
    static <E extends Enum<E>> ITuneAs<E> ENUM_INPUT(Class<E> enumType) {
        return (k,c,d,s,dg) -> new EnumTuner<>(c.getLayout(k),enumType, d, a -> {});
    }





}
