package org.bitbuckets.lib.core;

import edu.wpi.first.hal.HAL;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import org.bitbuckets.lib.ProcessPath;
import org.bitbuckets.lib.tune.IValueTuner;
import org.bitbuckets.lib.tune.TuneableDriver;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class TuneableDriverTest {

    @Test
    void tuneable_shouldBeGenerated() throws InterruptedException {
        assert HAL.initialize(500, 0);

        NetworkTable table = NetworkTableInstance.getDefault().getTable("RealOutputs/MattTuneables");
        IdentityDriver id = new IdentityDriver();
        TuneableDriver driver = new TuneableDriver(table, id);



        driver.tuneable(0, "a", 2.0);

        Thread.sleep(100);
        Assertions.assertTrue(table.containsKey("a"));
        Assertions.assertEquals(2.0, table.getEntry("a").getDouble(0.0));

        table.getEntry("a").setDouble(5.0);

        Thread.sleep(100);
        Assertions.assertEquals(5.0, table.getEntry("a").getDouble(0.0));
    }

    @Test
    void tuneable_shouldBeGeneratedProcessPath() throws InterruptedException {
        assert HAL.initialize(500, 0);

        NetworkTable table = NetworkTableInstance.getDefault().getTable("RealOutputs/MattTuneables");
        IdentityDriver id = new IdentityDriver();
        TuneableDriver driver = new TuneableDriver(table, id);
        ProcessPath path = new ProcessPath(
                0,
                Mockito.mock(),
                id,
                Mockito.mock(),
                Mockito.mock(),
                driver,
                false
        );


        IValueTuner<String> tuner = path.generateValueTuner("a", "hello");
        Thread.sleep(100);

        Assertions.assertTrue(table.getEntry("a").exists());
        Assertions.assertEquals("hello", table.getEntry("a").getString("bad"));
        Assertions.assertEquals("hello",tuner.readValue());

        table.getEntry("a").setString("test");
        Thread.sleep(100);

        Assertions.assertEquals("test", table.getEntry("a").getString("bad"));
        Assertions.assertEquals("test", tuner.readValue());

    }
}