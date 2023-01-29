package org.bitbuckets.lib.core;

import edu.wpi.first.math.kinematics.SwerveModuleState;
import org.littletonrobotics.junction.Logger;

/**
 * Users of the library should never see this
 * Allows users to report data to the logs optimally
 */
public class LogDriver {

    final Logger logger;
    final IdentityDriver identityDriver;

    public LogDriver(Logger logger, IdentityDriver identityDriver) {
        this.logger = logger;
        this.identityDriver = identityDriver;
    }

    public void report(int id, String keyName, double data) {
        logger.recordOutput(identityDriver.fullPath(id) + keyName, data);
    }

    public void report(int id, String keyName, SwerveModuleState[] moduleStates) {
        logger.recordOutput(identityDriver.fullPath(id) + keyName, moduleStates);
    }

    public void report(int id, String keyName, boolean bool) {
        logger.recordOutput(identityDriver.fullPath(id) + keyName, bool);
    }

    public void report(int id, String keyName, String str) {
        logger.recordOutput(identityDriver.fullPath(id) + keyName, str);
    }



}
