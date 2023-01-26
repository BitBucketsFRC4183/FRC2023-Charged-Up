package org.bitbuckets.lib.control;

import edu.wpi.first.math.controller.PIDController;
import org.bitbuckets.lib.tune.IValueTuner;

public class PidCalculator implements IPIDCalculator, Runnable {

    final PIDController controller;
    final IValueTuner<double[]> pidConstants;

    public PidCalculator(PIDController controller, IValueTuner<double[]> pidConstants) {
        this.controller = controller;
        this.pidConstants = pidConstants;
    }

    @Override
    public double calculateNext() {
        return 0;
    }

    @Override
    public void run() {

    }
}
