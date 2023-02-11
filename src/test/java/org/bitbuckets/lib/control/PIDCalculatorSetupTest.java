package org.bitbuckets.lib.control;

import edu.wpi.first.hal.HAL;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import org.bitbuckets.lib.ProcessPath;
import org.bitbuckets.lib.core.IdentityDriver;
import org.bitbuckets.lib.core.LoopDriver;
import org.bitbuckets.lib.tune.TuneableDriver;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class PIDCalculatorSetupTest {

    @Test
    void pidCalculator_shouldWork() throws InterruptedException {
        assert HAL.initialize(500, 0);

        NetworkTable table = NetworkTableInstance.getDefault().getTable("RealOutputs");

        IdentityDriver id = new IdentityDriver();
        TuneableDriver driver = new TuneableDriver(table, id);
        LoopDriver loopDriver = new LoopDriver();

        ProcessPath path = new ProcessPath(
                0,
                Mockito.mock(),
                id,
                Mockito.mock(),
                loopDriver,
                driver,
                false
        );

        IPIDCalculator calculator = new PIDCalculatorSetup(
                new PIDConfig(1,0,0,0)
        ).build(path.addChild("calculator"));

        Assertions.assertTrue(table.getEntry("calculator/p").exists());
        Assertions.assertEquals(1.0, calculator.rawAccess(PIDController.class).getP(), "initial P value should be 1");

        table.getEntry("calculator/p").setDouble(2.0);

        Thread.sleep(1000);
        loopDriver.runPeriodic();
        Assertions.assertEquals(2.0, calculator.rawAccess(PIDController.class).getP(), "pid calculator should read 2");

    }
}