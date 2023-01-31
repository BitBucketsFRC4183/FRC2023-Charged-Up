package org.bitbuckets.lib;

import edu.wpi.first.networktables.NetworkTableInstance;
import org.bitbuckets.lib.core.*;
import org.bitbuckets.lib.startup.SetupDriver;
import org.bitbuckets.lib.tune.TuneableDriver;
import org.littletonrobotics.junction.Logger;

public class ProcessPathUtil {

    public static ProcessPath testingProcessPath() {
        IdentityDriver identityDriver = new IdentityDriver();
        LogDriver logDriver = new LogDriver(Logger.getInstance(), identityDriver);
        LoopDriver loopDriver = new LoopDriver();
        TuneableDriver tuneableDriver = new TuneableDriver(NetworkTableInstance.getDefault().getTable("RealOutputs/MattTuneables"), identityDriver);
        SetupDriver setupDriver = new SetupDriver(identityDriver, logDriver, 0);

        return new ProcessPath(0, setupDriver, identityDriver, logDriver, loopDriver, tuneableDriver);
    }

}
