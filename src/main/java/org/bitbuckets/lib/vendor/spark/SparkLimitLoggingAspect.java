package org.bitbuckets.lib.vendor.spark;

import com.revrobotics.SparkMaxLimitSwitch;
import org.bitbuckets.lib.log.ILoggable;

public class SparkLimitLoggingAspect implements Runnable {

    final ILoggable<Boolean> data;
    final SparkMaxLimitSwitch limitSwitch;

    public SparkLimitLoggingAspect(ILoggable<Boolean> data, SparkMaxLimitSwitch limitSwitch) {
        this.data = data;
        this.limitSwitch = limitSwitch;
    }

    @Override
    public void run() {
        data.log(limitSwitch.isPressed());

    }
}
