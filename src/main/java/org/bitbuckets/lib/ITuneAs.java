package org.bitbuckets.lib;

import config.Mattlib;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.bitbuckets.lib.core.Path;
import org.bitbuckets.lib.tune.*;

public interface ITuneAs<T> {

    IValueTuner<T> generate(String key, Path path, T defaultData);

    ITuneAs<Double> DOUBLE_INPUT = (key, path, dat) -> {

        if (Mattlib.DEFAULT_MODE == ProcessMode.DEBUG) {
            var entry = NetworkTableInstance.getDefault().getTable("mattlib").getEntry(path.getAsTablePath() + "tune-"+ key);
            entry.setDouble(dat);

            return new CorrectnessTuner<>(entry);
        } else {
            return new NoopsTuner<>(dat);
        }



    };


    //lord this is cursed, networktables is a thing that causes me great pain
    static <E extends Enum<E>> ITuneAs<E> ENUM(Class<E> enumType) {
        return (k,path, dat) -> {

            SendableChooser<E> sendablechooser = new SendableChooser<>();
            sendablechooser.setDefaultOption(dat.name(), dat);

            for (E e : enumType.getEnumConstants()) {
                sendablechooser.addOption(e.name(), e);
            }

            SmartDashboard.putData(k, sendablechooser);


            return new ChooserTuner<>(sendablechooser);
        };

    }


}
