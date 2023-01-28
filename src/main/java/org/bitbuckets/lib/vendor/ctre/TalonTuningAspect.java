package org.bitbuckets.lib.vendor.ctre;

import com.ctre.phoenix.motorcontrol.can.TalonFX;
import org.bitbuckets.lib.tune.IValueTuner;

/**
 * Tunes a talon based on the funny tuneables
 */
@Deprecated
public class TalonTuningAspect implements Runnable {

    final TalonFX tuneableMotor;
    final IValueTuner<double[]> pidConstants;

    public TalonTuningAspect(TalonFX tuneableMotor, IValueTuner<double[]> pidConstants) {
        this.tuneableMotor = tuneableMotor;
        this.pidConstants = pidConstants;
    }

    @Override
    public void run() {
        if (pidConstants.hasUpdated()) {

            double[] pidConstantArray = pidConstants.consumeValue();


        }
    }
}
