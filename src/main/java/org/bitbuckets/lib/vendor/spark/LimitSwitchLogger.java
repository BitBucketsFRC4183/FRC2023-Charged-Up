package org.bitbuckets.lib.vendor.spark;

import com.revrobotics.SparkMaxLimitSwitch;
import org.bitbuckets.lib.CanLogLoop;
import org.bitbuckets.lib.log.ILoggable;

public class LimitSwitchLogger implements CanLogLoop {

    final ILoggable<Boolean> data;
    final SparkMaxLimitSwitch limitSwitch;

    public LimitSwitchLogger(ILoggable<Boolean> data, SparkMaxLimitSwitch limitSwitch) {
        this.data = data;
        this.limitSwitch = limitSwitch;
    }

    @Override
    public void logLoop() {
        data.log(limitSwitch.isPressed());
    }
}
