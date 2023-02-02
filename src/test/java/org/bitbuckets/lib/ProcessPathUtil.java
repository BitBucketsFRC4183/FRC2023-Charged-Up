package org.bitbuckets.lib;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import org.bitbuckets.lib.core.IdentityDriver;
import org.bitbuckets.lib.core.LogDriver;
import org.bitbuckets.lib.core.LoopDriver;
import org.bitbuckets.lib.startup.SetupDriver;
import org.bitbuckets.lib.tune.TuneableDriver;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.littletonrobotics.junction.Logger;

public class ProcessPathUtil {

    @Test
    public void testProcessPathShouldWork() {

        ProcessPath path = testingProcessPath();

        NetworkTableInstance.getDefault().getTable("RealOutputs/MattTuneables");


    }

    public static ProcessPath testingProcessPath() {
        IdentityDriver identityDriver = new IdentityDriver();
        LogDriver logDriver = new LogDriver(Logger.getInstance(), identityDriver);
        LoopDriver loopDriver = new LoopDriver();
        TuneableDriver tuneableDriver = new TuneableDriver(NetworkTableInstance.getDefault().getTable("RealOutputs/MattTuneables"), identityDriver);
        SetupDriver setupDriver = new SetupDriver(identityDriver, logDriver, 0);

        return new ProcessPath(0, setupDriver, identityDriver, logDriver, loopDriver, tuneableDriver, false);
    }

}
