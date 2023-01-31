package org.bitbuckets.lib.log.type;

import org.bitbuckets.lib.core.LogDriver;
import org.bitbuckets.lib.log.ILoggable;

public class DoubleLoggable implements ILoggable<Double> {

    final LogDriver driver;
    final int id;
    final String keyName;

    public DoubleLoggable(LogDriver driver, int id, String keyName) {
        this.driver = driver;
        this.id = id;
        this.keyName = keyName;
    }

    @Override
    public void log(Double data) {
        driver.report(id, keyName, data);
    }
}
