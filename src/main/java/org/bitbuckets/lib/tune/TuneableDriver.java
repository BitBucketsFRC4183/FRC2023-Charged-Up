package org.bitbuckets.lib.tune;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableEvent;
import edu.wpi.first.networktables.NetworkTableInstance;
import org.bitbuckets.lib.core.IdentityDriver;

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

    public <T> IValueTuner<T> tuneable(int id, String key, T defaultData) {
        String trueKey = driver.fullPath(id) + key;

        NetworkTableEntry entry = table.getEntry(trueKey);
        if (defaultData instanceof Enum) {
            entry.setDefaultValue(defaultData.toString());
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
