package org.bitbuckets.lib.network;

import edu.wpi.first.hal.HAL;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class LogFactoryTest {

    @BeforeAll
    public static void nt() {
        assert HAL.initialize(500, 0);

        //TODO please fix things
    }


    //TODO test logfactory
    @Test
    public void testDefaultCode() {
        SmartDashboard.putNumber("a", 5);
        double get = SmartDashboard.getNumber("a", 0);

        Assertions.assertEquals(5, get);
    }



}
