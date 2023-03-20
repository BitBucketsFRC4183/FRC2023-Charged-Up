package org.bitbuckets.lib.control;

import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.trajectory.TrapezoidProfile;
import org.bitbuckets.lib.log.ILoggable;
import org.bitbuckets.lib.tune.IValueTuner;

public class ProfiledPIDCalculator implements Runnable, IPIDCalculator {

    final ProfiledPIDController profiledPIDController;
    final IValueTuner<Double> p;
    final IValueTuner<Double> i;
    final IValueTuner<Double> d;
    final IValueTuner<Double> kV;
    final IValueTuner<Double> kA;

    final ILoggable<Double> lastSetpoint;
    final ILoggable<Double> lastActual;
    final ILoggable<Double> lastOutput;

    public ProfiledPIDCalculator(ProfiledPIDController profiledPIDController, IValueTuner<Double> p, IValueTuner<Double> i, IValueTuner<Double> d, IValueTuner<Double> kV, IValueTuner<Double> kA, ILoggable<Double> lastSetpoint, ILoggable<Double> lastActual, ILoggable<Double> lastOutput) {
        this.profiledPIDController = profiledPIDController;
        this.p = p;
        this.i = i;
        this.d = d;
        this.kV = kV;
        this.kA = kA;
        this.lastSetpoint = lastSetpoint;
        this.lastActual = lastActual;
        this.lastOutput = lastOutput;
    }

    @Override
    public void run() {
        if (p.hasUpdated()) {
            profiledPIDController.setP(p.consumeValue());
        }
        if (i.hasUpdated()) {
            profiledPIDController.setI(i.consumeValue());
        }
        if (d.hasUpdated()) {
            profiledPIDController.setD(d.consumeValue());
        }
        if (kV.hasUpdated() || kA.hasUpdated()) {
            profiledPIDController.setConstraints(new TrapezoidProfile.Constraints(kV.consumeValue(), kA.consumeValue()));
        }

        lastSetpoint.log(lastSetpointVal);
        lastActual.log(lastMeasureVal);
        lastOutput.log(lastVoltageVal);
    }

    @Override
    public double calculateNext(double measurement, double setpoint) {
        profiledPIDController.setGoal(setpoint);
        double output = profiledPIDController.calculate(measurement);
        lastVoltageVal = output;

        return output;
    }

    double lastSetpointVal = 0;
    double lastVoltageVal = 0;
    double lastMeasureVal = 0;

    @Override
    public double lastError() {
        return lastSetpointVal - lastMeasureVal;
    }

    @Override
    public <T> T rawAccess(Class<T> clazz) throws UnsupportedOperationException {
        return clazz.cast(profiledPIDController);
    }
}
