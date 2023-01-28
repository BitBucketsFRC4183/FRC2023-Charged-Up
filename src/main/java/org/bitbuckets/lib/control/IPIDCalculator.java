package org.bitbuckets.lib.control;

import org.bitbuckets.lib.DontUse;
import org.bitbuckets.lib.hardware.IRaw;

/**
 * This class acts like a {@link edu.wpi.first.math.controller.PIDController} except
 * it tunes pid internally. Construct using {@link PIDCalculatorSetup}
 */
@DontUse @Deprecated
public interface IPIDCalculator extends IRaw {

    /**
     * Calculates the next control output based on the current measurement and setpoint
     * @param measurement sensor measurement
     * @param setpoint where you want motor to go
     * @return the voltage, or whatever units this pid controller is in, that you want to go to
     */
    double calculateNext(double measurement, double setpoint);

}
