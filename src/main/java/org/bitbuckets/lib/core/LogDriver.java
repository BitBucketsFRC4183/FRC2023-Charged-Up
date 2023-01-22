package org.bitbuckets.lib.core;

import edu.wpi.first.math.kinematics.SwerveModuleState;
import org.littletonrobotics.junction.Logger;

/**
 * Users of the library should never see this
 * Allows users to report data to the logs optimally
 */
public class LogDriver {

    final IdentityDriver identityDriver;

    public LogDriver(IdentityDriver identityDriver) {
        this.identityDriver = identityDriver;
    }


    //TODO migrate
    //TODO threaded optimize + caching (most optimization done on Data side)
    public void report(int id, String keyName, double data) {
        Logger.getInstance().recordOutput(identityDriver.fullPath(id) + keyName, data);
    }

    public void report(int id, String keyName, SwerveModuleState[] moduleStates) {
        Logger.getInstance().recordOutput(identityDriver.fullPath(id) + keyName, moduleStates);
    }

    public void report(int id, String keyName, boolean bool) {
        Logger.getInstance().recordOutput(identityDriver.fullPath(id) + keyName, bool);
    }

    public void report(int id, String keyName, String str) {
        Logger.getInstance().recordOutput(identityDriver.fullPath(id) + keyName, str);
    }


}
