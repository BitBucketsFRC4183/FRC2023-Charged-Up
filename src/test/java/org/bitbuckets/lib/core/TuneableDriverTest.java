package org.bitbuckets.lib.core;

import edu.wpi.first.hal.HAL;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import org.bitbuckets.drive.DriveSubsystem;
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

        // each test needs a unique key in the table
        String key = "a";
        driver.tuneable(0, key, 2.0);

        Thread.sleep(1000);
        Assertions.assertTrue(table.containsKey(key));
        var entry = table.getEntry(key).getDouble(0.0);
        Assertions.assertEquals(2.0, entry);

        table.getEntry(key).setDouble(5.0);

        Thread.sleep(1000);
        Assertions.assertEquals(5.0, table.getEntry(key).getDouble(0.0));
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

        // each test needs a unique key in the table
        String key = "b";
        IValueTuner<String> tuner = path.generateValueTuner(key, "hello");
        Thread.sleep(100);

        Assertions.assertTrue(table.getEntry(key).exists());
        Assertions.assertEquals("hello", table.getEntry(key).getString("bad"));
        Assertions.assertEquals("hello", tuner.readValue());

        table.getEntry(key).setString("test");
        Thread.sleep(100);

        Assertions.assertEquals("test", table.getEntry(key).getString("bad"));
        Assertions.assertEquals("test", tuner.readValue());

    }

    @Test
    void tuneable_shouldWorkWithEnum() throws InterruptedException {
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

        // each test needs a unique key in the table
        String key = "c";
        IValueTuner<DriveSubsystem.OrientationChooser> tuner = path.generateEnumTuner(key, DriveSubsystem.OrientationChooser.class, DriveSubsystem.OrientationChooser.FIELD_ORIENTED);
        Thread.sleep(100);

        NetworkTableEntry theThingThatActuallyChanges = table.getSubTable(key).getEntry("selected");

        Assertions.assertTrue(theThingThatActuallyChanges.exists());
        Assertions.assertEquals(DriveSubsystem.OrientationChooser.FIELD_ORIENTED, tuner.readValue());

        theThingThatActuallyChanges.setString(DriveSubsystem.OrientationChooser.ROBOT_ORIENTED.toString());
        Thread.sleep(100);

        Assertions.assertEquals(DriveSubsystem.OrientationChooser.ROBOT_ORIENTED, tuner.readValue());

    }
}