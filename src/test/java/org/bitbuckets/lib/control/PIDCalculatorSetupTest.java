package org.bitbuckets.lib.control;

import edu.wpi.first.hal.HAL;
import org.bitbuckets.lib.ProcessPath;
import org.bitbuckets.lib.ProcessPathUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PIDCalculatorSetupTest {

    @Test
    void pidCalculator_shouldWork() {
        assert HAL.initialize(500, 0);

        ProcessPath path = ProcessPathUtil.testingProcessPath();
        IPIDCalculator calculator = new PIDCalculatorSetup().build(path);


    }
}