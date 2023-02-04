package org.bitbuckets.lib.tune;

import edu.wpi.first.hal.HAL;
import edu.wpi.first.networktables.NetworkTableInstance;
import org.bitbuckets.lib.tune.type.ValueTuner;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class
ValueTunerTest {

    @Disabled
    @Test
    void readValue_shouldWork() throws InterruptedException {
        assert HAL.initialize(500, 0);

        ValueTuner<Double> tuner = new ValueTuner<>(20.0);
        NetworkTableInstance
                .getDefault()
                .getTable("frc")
                .getEntry("hello")
                .setDefaultValue(20.0);

        NetworkTableInstance
                .getDefault()
                .getTable("frc")
                .getEntry("hello")
                .setDouble(30.0);

        Thread.sleep(100);

        double pulledFirst = tuner.readValue();
        Assertions.assertEquals(30, pulledFirst);

        boolean hasUpdated = tuner.hasUpdated();
        Assertions.assertTrue(hasUpdated);

        double pulled = tuner.consumeValue();
        Assertions.assertEquals(30, pulled);

        boolean hasUpdated2 = tuner.hasUpdated();
        Assertions.assertFalse(hasUpdated2);

    }
}