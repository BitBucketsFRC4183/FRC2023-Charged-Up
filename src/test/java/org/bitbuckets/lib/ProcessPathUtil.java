package org.bitbuckets.lib;

import edu.wpi.first.networktables.NetworkTableInstance;
import org.bitbuckets.lib.core.*;
import org.bitbuckets.lib.startup.SetupDriver;
import org.littletonrobotics.junction.Logger;

public class ProcessPathUtil {

    public static ProcessPath testingProcessPath() {
        IdentityDriver identityDriver = new IdentityDriver();
        ErrorDriver errorDriver = new ErrorDriver(identityDriver);
        LogDriver logDriver = new LogDriver(Logger.getInstance(), identityDriver);
        LoopDriver loopDriver = new LoopDriver();
        TuneableDriver tuneableDriver = new TuneableDriver(NetworkTableInstance.getDefault().getTable("preferences"), identityDriver);
        SetupDriver setupDriver = new SetupDriver(identityDriver, logDriver, 0);

        return new ProcessPath(0, setupDriver, identityDriver, errorDriver, logDriver, loopDriver, tuneableDriver);
    }

}
