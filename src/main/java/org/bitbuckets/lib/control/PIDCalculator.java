package org.bitbuckets.lib.control;

import edu.wpi.first.math.controller.PIDController;
import org.bitbuckets.lib.log.Debuggable;
import org.bitbuckets.lib.tune.IValueTuner;

public class PIDCalculator implements IPIDCalculator, Runnable {

    final PIDController controller;
    final Debuggable debuggable;
    final IValueTuner<double[]> tuner;

    public PIDCalculator(PIDController controller, Debuggable debuggable, IValueTuner<double[]> tuner) {
        this.controller = controller;
        this.debuggable = debuggable;
        this.tuner = tuner;
    }

    double lastSetpoint = 0;
    double lastVoltage = 0;
    double lastMeasure = 0;

    @Override
    public double calculateNext(double measurement, double setpoint) {

        lastMeasure = measurement;
        controller.setSetpoint(setpoint);
        lastSetpoint = setpoint;
        lastVoltage = controller.calculate(measurement);



        return lastVoltage * 12;
    }

    @Override
    public void run() {

        debuggable.log("setpoint", controller.getSetpoint());
        debuggable.log("error", controller.getPositionError());
        debuggable.log("actual", lastMeasure);
        debuggable.log("lastOutput", lastVoltage);

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
