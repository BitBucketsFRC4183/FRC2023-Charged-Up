package org.bitbuckets.lib.control;

import edu.wpi.first.hal.HAL;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import org.bitbuckets.lib.ProcessPath;
import org.bitbuckets.lib.ProcessPathUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class PIDCalculatorSetupTest {

    @Test
    void pidCalculator_shouldWork() throws InterruptedException {
        assert HAL.initialize(500, 0);

        ProcessPath path = ProcessPathUtil.testingProcessPath();
        IPIDCalculator calculator = new PIDCalculatorSetup(
                new PIDConfig(1,0,0,0)
        ).build(path.addChild("calculator"));

        Assertions.assertTrue(NetworkTableInstance.getDefault().getTable("RealOutputs/MattTuneables").getEntry("calculator").exists());

        NetworkTableEntry e = NetworkTableInstance.getDefault().getTable("RealOutputs/MattTuneables").getEntry("calculator");

        Assertions.assertTrue(e.exists(), "nettable entry should exist");
        Assertions.assertTrue(e.isValid(), "nettable entry should be valid");

        Assertions.assertEquals(1.0, calculator.rawAccess(PIDController.class).getP(), "initial P value should be 1");

        NetworkTableInstance.getDefault().getTable("RealOutputs/MattTuneables").getEntry("calculator/p").setDouble(2.0);

        Thread.sleep(1000);
        Assertions.assertEquals(2.0, calculator.rawAccess(PIDController.class).getP(), "pid calculator should read 2");

    }
}