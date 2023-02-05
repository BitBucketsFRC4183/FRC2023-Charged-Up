package org.bitbuckets.lib.core;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import org.bitbuckets.lib.log.ILoggable;
import org.bitbuckets.lib.log.type.DoubleLoggable;
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

    public ILoggable<Double> generateDoubleLoggable(int id, String key) {
        return new DoubleLoggable(this, id, key);
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

    public void report(int id, String keyName, Pose2d pose) {
        logger.recordOutput(identityDriver.fullPath(id) + keyName, pose);
    }

    public void report(int id, String keyName, Pose3d pose) {
        logger.recordOutput(identityDriver.fullPath(id) + keyName, pose);
    }

}
