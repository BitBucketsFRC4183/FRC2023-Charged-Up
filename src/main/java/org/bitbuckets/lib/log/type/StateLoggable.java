package org.bitbuckets.lib.log.type;

import edu.wpi.first.math.kinematics.SwerveModuleState;
import org.bitbuckets.lib.DontUseIncubating;
import org.bitbuckets.lib.core.LogDriver;
import org.bitbuckets.lib.log.ILoggable;

public class StateLoggable implements ILoggable<SwerveModuleState[]> {

    final LogDriver driver;
    final int processId;
    final String name;

    public StateLoggable(LogDriver driver, int processId, String name) {
        this.driver = driver;
        this.processId = processId;
        this.name = name;
    }

    @Override
    public void log(SwerveModuleState[] data) {
        driver.report(processId, name, data);
    }
}
