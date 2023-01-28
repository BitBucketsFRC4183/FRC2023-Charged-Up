package org.bitbuckets.lib.vendor.spark;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel;
import com.revrobotics.SparkMaxPIDController;
import edu.wpi.first.hal.HAL;
import org.bitbuckets.lib.tune.IValueTuner;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

class SparkTuningAspectTest {


    @Test
    public void shouldSetCanConstants_light() {
        IValueTuner<double[]> tuner = Mockito.mock(IValueTuner.class);
        Mockito.when(tuner.hasUpdated()).thenReturn(true);
        Mockito.when(tuner.consumeValue()).thenReturn(new double[] { 1.0, 2.0, 3.0, 4.0, 5.0 });

        SparkMaxPIDController canSparkMax = Mockito.mock(SparkMaxPIDController.class);
        SparkTuningAspect tuningAspect = new SparkTuningAspect(tuner, canSparkMax);

        tuningAspect.run(); //it's been ran

        Mockito.verify(canSparkMax, Mockito.times(1)).setP(1.0);
        Mockito.verify(canSparkMax, Mockito.times(1)).setI(2.0);
        Mockito.verify(canSparkMax, Mockito.times(1)).setD(3.0);
        Mockito.verify(canSparkMax, Mockito.times(1)).setFF(4.0);
        Mockito.verify(canSparkMax, Mockito.times(1)).setIZone(5.0);
    }


    @Test
    public void shouldTestCanConstants_simulated() throws InterruptedException {
        assert HAL.initialize(500, 0);

        IValueTuner<double[]> tuner = Mockito.mock(IValueTuner.class);
        Mockito.when(tuner.hasUpdated()).thenReturn(true);
        Mockito.when(tuner.consumeValue()).thenReturn(new double[] { 1.0, 2.0, 3.0, 4.0, 5.0 });
        CANSparkMax sparkMax = new CANSparkMax(0, CANSparkMaxLowLevel.MotorType.kBrushless);

        SparkTuningAspect tuningAspect = new SparkTuningAspect(tuner, sparkMax.getPIDController());
        tuningAspect.run(); //load ur data boy

        Thread.sleep(500);

        Assertions.assertEquals(1.0, tuningAspect.sparkMaxPIDController.getP());
    }

}