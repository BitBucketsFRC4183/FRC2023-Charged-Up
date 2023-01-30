package org.bitbuckets.lib.log.type;

import org.bitbuckets.lib.DontUseIncubating;
import org.bitbuckets.lib.core.LogDriver;
import org.bitbuckets.lib.log.ILoggable;

@Deprecated @DontUseIncubating
public class StringLoggable implements ILoggable<String> {

    final LogDriver driver;
    final int id;
    final String keyName;

    public StringLoggable(LogDriver driver, int id, String keyName) {
        this.driver = driver;
        this.id = id;
        this.keyName = keyName;
    }

    @Override
    public void log(String data) {
        driver.report(id, keyName, data);
    }
}
