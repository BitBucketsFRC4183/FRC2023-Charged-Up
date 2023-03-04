package org.bitbuckets.lib.control;

import edu.wpi.first.math.controller.PIDController;
import org.bitbuckets.lib.core.HasLogLoop;
import org.bitbuckets.lib.core.HasLoop;
import org.bitbuckets.lib.log.ILoggable;
import org.bitbuckets.lib.tune.IValueTuner;

public class PIDCalculator implements IPIDCalculator, HasLogLoop {

    final PIDController controller;

    final IValueTuner<Double> p;
    final IValueTuner<Double> i;
    final IValueTuner<Double> d;

    final ILoggable<Double> lastSetpoint;
    final ILoggable<Double> lastActual;
    final ILoggable<Double> lastOutput;


    public PIDCalculator(PIDController controller, IValueTuner<Double> p, IValueTuner<Double> i, IValueTuner<Double> d, ILoggable<Double> lastSetpoint, ILoggable<Double> lastActual, ILoggable<Double> lastOutput) {
        this.controller = controller;
        this.p = p;
        this.i = i;
        this.d = d;
        this.lastSetpoint = lastSetpoint;
        this.lastActual = lastActual;
        this.lastOutput = lastOutput;
    }

    double lastSetpointVal = 0;
    double lastVoltageVal = 0;
    double lastMeasureVal = 0;

    @Override
    public double calculateNext(double measurement, double setpoint) {
        controller.setSetpoint(setpoint);
        double voltage = 12.0 * controller.calculate(measurement);


        lastMeasureVal = measurement;
        lastSetpointVal = setpoint;
        lastVoltageVal = voltage;
        return voltage;
    }

    @Override
    public double lastError() {
        return lastSetpointVal - lastMeasureVal;
    }


    @Override
    public <T> T rawAccess(Class<T> clazz) throws UnsupportedOperationException {
        return clazz.cast(controller);
    }

    @Override
    public void logLoop() {
        lastSetpoint.log(lastSetpointVal);
        lastActual.log(lastMeasureVal);
        lastOutput.log(lastVoltageVal);

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


}
