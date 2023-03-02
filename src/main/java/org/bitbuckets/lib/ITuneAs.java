package org.bitbuckets.lib;

import edu.wpi.first.networktables.NetworkTableEvent;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardContainer;
import org.bitbuckets.lib.tune.IValueTuner;
import org.bitbuckets.lib.tune.ChooserTuner;
import org.bitbuckets.lib.tune.ValueTuner;

import java.util.EnumSet;

public interface ITuneAs<T> {

    EnumSet<NetworkTableEvent.Kind> REMOTE = EnumSet.of(NetworkTableEvent.Kind.kValueAll);
    IValueTuner<T> generate(String key, ShuffleboardContainer container, T data, IValueTuner<ProcessMode> self);

    ITuneAs<Double> DOUBLE_INPUT = (k,c,d,s) -> {


        var entry = c.add(k, d).getEntry();
        var tuneable = new ValueTuner<>(entry, s, d);
        NetworkTableInstance.getDefault().addListener(entry, REMOTE, tuneable);

        return tuneable;
    };


    //lord this is cursed, networktables is a thing that causes me great pain
    static <E extends Enum<E>> ITuneAs<E> ENUM(Class<E> enumType) {
        return (k,c,d,s) -> {
            var chooser = new ChooserTuner<>(enumType, d);

            for (E e : enumType.getEnumConstants()) {
                chooser.addOption(e.name(), e);
            }

            c.add(chooser);

            return chooser;
        };

    }

    class Random {
        static int random = 0;
    }


}
