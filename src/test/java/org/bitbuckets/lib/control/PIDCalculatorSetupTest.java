package org.bitbuckets.lib.control;

import edu.wpi.first.hal.HAL;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.networktables.NetworkTableInstance;
import org.bitbuckets.lib.ProcessPath;
import org.bitbuckets.lib.ProcessPathUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class PIDCalculatorSetupTest {

    @Test
    void pidCalculator_shouldWork() {
        assert HAL.initialize(500, 0);

        ProcessPath path = ProcessPathUtil.testingProcessPath();
        IPIDCalculator calculator = new PIDCalculatorSetup().build(path.addChild("calculator"));

        NetworkTableInstance.getDefault().getTable("calculator").getEntry("p").setDouble(2.0);

        Assertions.assertEquals(2.0, calculator.rawAccess(PIDController.class).getP());

    }
}