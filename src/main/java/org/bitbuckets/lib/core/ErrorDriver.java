package org.bitbuckets.lib.core;

import edu.wpi.first.wpilibj.DriverStation;

/**
 * Allows ISetup to log critical errors that prevent the robot from starting
 * TODO: Rename ErrorManager to StartupFailer becuase it fails a robot during startup
 */
@Deprecated
public class ErrorDriver {

    final IdentityDriver identityFactory;

    public ErrorDriver(IdentityDriver identityFactory) {
        this.identityFactory = identityFactory;
    }

    public void flagError(int process, String error) {
        DriverStation.reportError(String.format("[BUCKET] process id [%s] failed with error [%s]", process, error), false);
        throw new IllegalStateException("[BUCKET] Fail Fast Guard Exception");
    }
}
