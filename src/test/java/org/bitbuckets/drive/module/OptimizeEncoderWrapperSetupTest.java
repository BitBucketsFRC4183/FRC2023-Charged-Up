package org.bitbuckets.drive.module;

import com.ctre.phoenix.sensors.*;
import edu.wpi.first.hal.HAL;
import org.bitbuckets.lib.util.CTRETestUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

class OptimizeEncoderWrapperSetupTest {


    @Test
    void canCoder_shouldProperlyBootTest() {
        assert HAL.initialize(500,0);

        CANCoderConfiguration config = new CANCoderConfiguration();

        config.initializationStrategy = SensorInitializationStrategy.BootToAbsolutePosition;
        config.sensorCoefficient = 2 * Math.PI / 4096.0;
        config.unitString = "rad";
        config.sensorTimeBase = SensorTimeBase.PerSecond;
        config.magnetOffsetDegrees = -180; //it's off by -180 degrees
        config.enableOptimizations = true;

        WPI_CANCoder cancoder = new WPI_CANCoder(0);
        cancoder.configAllSettings(config);

        CTRETestUtil.waitForCTREUpdate();

        //so it adds back 180degrees to compensate
        Assertions.assertEquals(Math.PI, cancoder.getAbsolutePosition(), 0.01);

    }

    @Test
    void build_shouldSetAbsoluteEncoderCorrectly() {

    }

    @Test
    void testGenerateStartingPosition_encoderRadians() {
        assert HAL.initialize(500, 0);
        WPI_CANCoder canCoder = new WPI_CANCoder(0);



    }


    @Test
    void forcePositionToEncoder() {
    }
}