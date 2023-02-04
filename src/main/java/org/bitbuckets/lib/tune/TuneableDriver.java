package org.bitbuckets.lib.tune;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableEvent;
import edu.wpi.first.networktables.NetworkTableInstance;
import org.bitbuckets.lib.core.IdentityDriver;
import org.bitbuckets.lib.tune.type.EnumTuner;
import org.bitbuckets.lib.tune.type.MultiValueTuner;
import org.bitbuckets.lib.tune.type.ValueTuner;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

/**
 * Makes sure we know if certain parts of the robot are breaking or not
 */
public class TuneableDriver {

    final NetworkTable table;
    final IdentityDriver driver;

    public TuneableDriver(NetworkTable table, IdentityDriver driver) {
        this.table = table;
        this.driver = driver;
    }


    public <T extends Enum<T>> IValueTuner<T> enumTuneable(int id, Class<T> enumClass, String key, T defaultData) {

        String trueKey = driver.fullPath(id) + key;

        NetworkTableEntry entry = table.getEntry(trueKey);
        entry.setDefaultValue(defaultData.toString());

        entry.setPersistent();

        //this probably isn't safe
        EnumTuner<T> enumTuner;
        try {
            T current = Enum.valueOf(enumClass, entry.getValue().getString());
            enumTuner = new EnumTuner<>(enumClass, current);
        } catch (Exception e) {
            System.err.println(String.format("%s is not part of %s, using default", entry.getValue().getString(), enumClass.getSimpleName()));
            enumTuner = new EnumTuner<>(enumClass, defaultData);
        }

        NetworkTableInstance.getDefault().addListener(entry, EnumSet.of(NetworkTableEvent.Kind.kValueAll), enumTuner);


        return enumTuner;
    }

    @Deprecated
    public <T> IValueTuner<T> tuneable(int id, String key, T defaultData) {
        String trueKey = driver.fullPath(id) + key;

        NetworkTableEntry entry = table.getEntry(trueKey);
        if (defaultData instanceof Enum) {
            throw new RuntimeException("cannot use tuneable() with enum. Use enumTunable()");
        } else {
            entry.setDefaultValue(defaultData);
        }

        entry.setPersistent();

        //this probably isn't safe
        T current = (T) entry.getValue().getValue();
        ValueTuner<T> tuneable = new ValueTuner<>(current);

        NetworkTableInstance.getDefault().addListener(entry, EnumSet.of(NetworkTableEvent.Kind.kValueAll), tuneable);

        return tuneable;
    }

    @SuppressWarnings("unchecked")
    public IValueTuner<double[]> multiTuneable(int id, String[] ids, double[] defaultValues) {
        List<IValueTuner<Double>> tunners = new ArrayList<>();

        for (int i = 0; i < ids.length; i++) {
            tunners.add(tuneable(id, ids[i], defaultValues[i]));
        }

        return new MultiValueTuner(tunners.toArray(IValueTuner[]::new));
    }

}
