package org.bitbuckets.drive.module;

import com.ctre.phoenix.sensors.*;
import edu.wpi.first.hal.HAL;
import org.bitbuckets.drive.fenc.FilteredEncoderSetup;
import org.bitbuckets.lib.util.CTRETestUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

class FilteredEncoderSetupTest {


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
    void retrieveCanCoderReadout_mechanismRadians() {

        WPI_CANCoder canCoder = Mockito.mock();

        Mockito.when(canCoder.getAbsolutePosition()).then(new Answer<Object>() {
            int i = 0;
            double[] pretendPositions = new double[] { 0.0, 0.0, 0.1, 0.7, 0.2, 0.3, 0.3, 0.3, 0.3, 0.4, 0.4}; //should return 0.3

            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {

                i++;

                return pretendPositions[i];
            }
        });

        double extracted = new FilteredEncoderSetup(0, Mockito.mock()).retrieveCanCoderReadout_mechanismRadians(canCoder);

        Assertions.assertNotEquals(0.0, extracted, 0.0, "should wait for similar values");
        Assertions.assertEquals(0.3, extracted, "should return on similar values");

    }

    @Test
    void forcePositionToEncoder() {
    }
}