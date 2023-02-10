package org.bitbuckets.lib.vendor.spark;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel;
import com.revrobotics.SparkMaxPIDController;
import edu.wpi.first.hal.HAL;
import org.bitbuckets.lib.tune.IValueTuner;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class SparkTunerTest {


    @Test
    public void shouldSetCanConstants_light() {
        IValueTuner<Double> tuner = Mockito.mock(IValueTuner.class);
        Mockito.when(tuner.hasUpdated()).thenReturn(true);
        Mockito.when(tuner.consumeValue()).thenReturn(1.0);

        SparkMaxPIDController canSparkMax = Mockito.mock(SparkMaxPIDController.class);
        SparkTuner tuningAspect = new SparkTuner(tuner, tuner, tuner, canSparkMax);

        tuningAspect.run(); //it's been ran

        Mockito.verify(canSparkMax, Mockito.times(1)).setP(1.0);
        Mockito.verify(canSparkMax, Mockito.times(1)).setI(1.0);
        Mockito.verify(canSparkMax, Mockito.times(1)).setD(1.0);
    }


    @Test
    public void shouldTestCanConstants_simulated() throws InterruptedException {
        assert HAL.initialize(500, 0);

        IValueTuner<Double> tuner = Mockito.mock(IValueTuner.class);
        Mockito.when(tuner.hasUpdated()).thenReturn(true);
        Mockito.when(tuner.consumeValue()).thenReturn(1.0);
        CANSparkMax sparkMax = new CANSparkMax(0, CANSparkMaxLowLevel.MotorType.kBrushless);

        SparkTuner tuningAspect = new SparkTuner(tuner, tuner, tuner, sparkMax.getPIDController());
        tuningAspect.run(); //load ur data boy

        Thread.sleep(500);

        Assertions.assertEquals(1.0, tuningAspect.sparkMaxPIDController.getP());
    }

}