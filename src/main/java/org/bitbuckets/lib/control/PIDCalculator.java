package org.bitbuckets.lib.control;

import edu.wpi.first.math.controller.PIDController;
import org.bitbuckets.lib.DontUseIncubating;
import org.bitbuckets.lib.tune.IValueTuner;

@DontUseIncubating
@Deprecated
public class PIDCalculator implements IPIDCalculator, Runnable {

    final PIDController controller;
    final IValueTuner<Double> p;
    final IValueTuner<Double> i;
    final IValueTuner<Double> d;

    PIDCalculator(PIDController controller, IValueTuner<Double> p, IValueTuner<Double> i, IValueTuner<Double> d) {
        this.controller = controller;
        this.p = p;
        this.i = i;
        this.d = d;
    }

    @Override
    public double calculateNext(double measurement, double setpoint) {
        return controller.calculate(measurement, setpoint);
    }

    @Override
    public void run() {
        if (p.hasUpdated()) {
            controller.setP(p.consumeValue());
        }

        if (i.hasUpdated()) {
            controller.setI(i.consumeValue());
        }

        if (d.hasUpdated()) {
            controller.setD(d.consumeValue());
        }
    }

    @Override
    public <T> T rawAccess(Class<T> clazz) throws UnsupportedOperationException {
        return clazz.cast(controller);
    }
}
