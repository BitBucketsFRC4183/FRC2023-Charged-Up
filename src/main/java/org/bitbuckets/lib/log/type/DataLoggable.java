package org.bitbuckets.lib.log.type;

import org.bitbuckets.lib.core.LogDriver;
import org.bitbuckets.lib.log.ILoggable;

public class DataLoggable implements ILoggable<double[]> {

    final String[] keys;
    final LogDriver driver;
    final int processId;

    public DataLoggable(String[] keys, LogDriver driver, int processId) {
        this.keys = keys;
        this.driver = driver;
        this.processId = processId;
    }

    @Override
    public void log(double[] data) {
        for (int i = 0; i < keys.length; i++) {
            driver.report(
                    processId,
                    keys[i],
                    data[i]
            );
        }
    }
}
