package org.bitbuckets.lib.vendor.spark;

import com.revrobotics.SparkMaxPIDController;
import org.bitbuckets.lib.hardware.PIDIndex;
import org.bitbuckets.lib.tune.IValueTuner;

public class SparkTuningAspect implements Runnable {

    final IValueTuner<Double> pTuner;
    final IValueTuner<Double> iTuner;
    final IValueTuner<Double> dTuner;
    final SparkMaxPIDController sparkMaxPIDController;

    SparkTuningAspect(IValueTuner<Double> pTuner, IValueTuner<Double> iTuner, IValueTuner<Double> dTuner, SparkMaxPIDController sparkMaxPIDController) {
        this.pTuner = pTuner;
        this.iTuner = iTuner;
        this.dTuner = dTuner;
        this.sparkMaxPIDController = sparkMaxPIDController;
    }

    @Override
    public void run() {

        if (pTuner.hasUpdated()) {
            sparkMaxPIDController.setP(pTuner.consumeValue());
        }

        if (iTuner.hasUpdated()) {
            sparkMaxPIDController.setI(iTuner.consumeValue());
        }


        if (dTuner.hasUpdated()) {
            sparkMaxPIDController.setD(dTuner.consumeValue());
        }

    }
}
