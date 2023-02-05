package org.bitbuckets.lib.log.type;

import edu.wpi.first.math.geometry.Pose2d;
import org.bitbuckets.lib.core.LogDriver;
import org.bitbuckets.lib.log.ILoggable;

public class Pose2dLoggable implements ILoggable<Pose2d> {

    final LogDriver driver;
    final int id;
    final String keyName;

    public Pose2dLoggable(LogDriver driver, int id, String keyName) {
        this.driver = driver;
        this.id = id;
        this.keyName = keyName;
    }

    @Override
    public void log(Pose2d data) {
        driver.report(id, keyName, data);
    }
}
