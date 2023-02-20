package org.bitbuckets.lib.control;

import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.trajectory.TrapezoidProfile;
import edu.wpi.first.math.util.Units;
import org.bitbuckets.lib.tune.IValueTuner;

public class ProfiledPIDFCalculator implements Runnable, IPIDCalculator {

    final ProfiledPIDController profiledPIDController;
    final IValueTuner<Double> p;
    final IValueTuner<Double> i;
    final IValueTuner<Double> d;
    final IValueTuner<Double> kV;
    final IValueTuner<Double> kA;

    public ProfiledPIDFCalculator(ProfiledPIDController profiledPIDController, IValueTuner<Double> p, IValueTuner<Double> i, IValueTuner<Double> d, IValueTuner<Double> kV, IValueTuner<Double> kA) {
        this.profiledPIDController = profiledPIDController;
        this.p = p;
        this.i = i;
        this.d = d;
        this.kV = kV;
        this.kA = kA;
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


    }

    @Override
    public double calculateNext(double measurement, double setpoint) {
        return profiledPIDController.calculate(measurement, setpoint);
    }

    @Override
    public <T> T rawAccess(Class<T> clazz) throws UnsupportedOperationException {
        return clazz.cast(profiledPIDController);
    }
}
