package org.bitbuckets.lib.vendor.ctre;

import com.ctre.phoenix.motorcontrol.can.TalonFX;
import org.bitbuckets.lib.hardware.PIDIndex;
import org.bitbuckets.lib.tune.ValueTuner;

/**
 * Tunes a talon based on the funny tuneables
 */
public class TalonTuningAspect implements Runnable {

    final TalonFX tuneableMotor;
    final ValueTuner<double[]> pidConstants;

    public TalonTuningAspect(TalonFX tuneableMotor, ValueTuner<double[]> pidConstants) {
        this.tuneableMotor = tuneableMotor;
        this.pidConstants = pidConstants;
    }

    @Override
    public void run() {
        if (pidConstants.hasUpdated()) {

            double[] pidConstantArray = pidConstants.consumeValue();

            tuneableMotor.config_kP(0, pidConstantArray[PIDIndex.P]);
            tuneableMotor.config_kI(0, pidConstantArray[PIDIndex.I]);
            tuneableMotor.config_kD(0, pidConstantArray[PIDIndex.D]);
            tuneableMotor.config_kF(0, pidConstantArray[PIDIndex.FF]);
            tuneableMotor.config_IntegralZone(0, pidConstantArray[PIDIndex.IZONE]);

        }
    }
}
