package org.bitbuckets.lib.control;

import edu.wpi.first.math.controller.PIDController;
import org.bitbuckets.lib.DontUseIncubating;
import org.bitbuckets.lib.tune.IValueTuner;

public class PIDCalculator implements IPIDCalculator, Runnable {

    final PIDController controller;
    final IValueTuner<double[]> tuner;

    public PIDCalculator(PIDController controller, IValueTuner<double[]> tuner) {
        this.controller = controller;
        this.tuner = tuner;
    }

    @Override
    public double calculateNext(double measurement, double setpoint) {
        return controller.calculate(measurement, setpoint);
    }

    @Override
    public void run() {

        if (tuner.hasUpdated()) {
            double[] pidArray = tuner.consumeValue();

            controller.setP(pidArray[PIDConfig.P]);
            controller.setI(pidArray[PIDConfig.I]);
            controller.setD(pidArray[PIDConfig.D]);
            controller.setD(pidArray[PIDConfig.F]);
        }
    }

    @Override
    public <T> T rawAccess(Class<T> clazz) throws UnsupportedOperationException {
        return clazz.cast(controller);
    }
}
