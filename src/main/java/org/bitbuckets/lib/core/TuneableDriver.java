package org.bitbuckets.lib.core;

import edu.wpi.first.networktables.*;
import edu.wpi.first.wpilibj.DriverStation;
import org.bitbuckets.lib.tune.ValueTuner;

import java.util.EnumSet;

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

    public <T> ValueTuner<T> tuneable(int id, String key, T defaultData) {
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

}
