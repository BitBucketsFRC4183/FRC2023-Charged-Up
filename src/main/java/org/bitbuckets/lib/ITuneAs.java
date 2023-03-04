package org.bitbuckets.lib;

import edu.wpi.first.networktables.GenericEntry;
import edu.wpi.first.networktables.NetworkTableEvent;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardContainer;
import org.bitbuckets.lib.process.RegisterType;
import org.bitbuckets.lib.tune.IValueTuner;
import org.bitbuckets.lib.tune.ChooserTuner;
import org.bitbuckets.lib.tune.ValueTuner;

import java.util.EnumSet;

public interface ITuneAs<T> {

    EnumSet<NetworkTableEvent.Kind> REMOTE = EnumSet.of(NetworkTableEvent.Kind.kValueRemote);

    IValueTuner<T> generate(String key, IDoWhenReady doWhenReady, T defaultData, IValueTuner<ProcessMode> self);

    ITuneAs<Double> DOUBLE_INPUT = (k, dwr, df , s) -> {


        var entry = dwr.doWhenReady(container -> {
           return container.add(k, df).getEntry();
        }, RegisterType.TUNE);

        ValueTuner<Double> tuneable = new ValueTuner<>(entry, s, df);

        entry.thenAccept( e -> {
            NetworkTableInstance.getDefault().addListener(e, REMOTE, tuneable);
        });

        return tuneable;
    };


    //lord this is cursed, networktables is a thing that causes me great pain
    static <E extends Enum<E>> ITuneAs<E> ENUM(Class<E> enumType) {
        return (k,dwr,df,s) -> {
            var chooser = new ChooserTuner<>(enumType, df);

            for (E e : enumType.getEnumConstants()) {
                chooser.addOption(e.name(), e);
            }

            dwr.doWhenReady(
                    board -> {
                        board.add(k, chooser);
                    },
                    RegisterType.TUNE
            );

            return chooser;
        };

    }

    static <E extends Enum<E>> ITuneAs<E> SIDE_ENUM(Class<E> enumType) {
        return (k,dwr,df,s) -> {
            var chooser = new ChooserTuner<>(enumType, df);

            for (E e : enumType.getEnumConstants()) {
                chooser.addOption(e.name(), e);
            }

            dwr.doWhenReady(
                    board -> {
                        board.add(k, chooser);
                    },
                    RegisterType.SIDEBAR
            );

            return chooser;
        };

    }

}
