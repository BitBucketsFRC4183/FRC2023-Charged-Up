package org.bitbuckets.lib.vendor.spark;

import com.revrobotics.SparkMaxPIDController;
import org.bitbuckets.lib.hardware.PIDIndex;
import org.bitbuckets.lib.tune.IValueTuner;

public class SparkTuningAspect implements Runnable {

    final IValueTuner<double[]> pidTuner;
    final SparkMaxPIDController sparkMaxPIDController;

    SparkTuningAspect(IValueTuner<double[]> pidTuner, SparkMaxPIDController sparkMaxPIDController) {
        this.pidTuner = pidTuner;
        this.sparkMaxPIDController = sparkMaxPIDController;
    }

    @Override
    public void run() {
        if (pidTuner.hasUpdated()) {
            double[] oo = pidTuner.consumeValue();

            sparkMaxPIDController.setP(oo[PIDIndex.P]);
            sparkMaxPIDController.setI(oo[PIDIndex.I]);
            sparkMaxPIDController.setD(oo[PIDIndex.D]);
            sparkMaxPIDController.setIZone(oo[PIDIndex.IZONE]);
            sparkMaxPIDController.setFF(oo[PIDIndex.FF]);

        }
    }
}
