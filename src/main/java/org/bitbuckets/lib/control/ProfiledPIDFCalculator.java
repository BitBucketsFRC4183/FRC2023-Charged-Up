package org.bitbuckets.lib.control;

import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.trajectory.TrapezoidProfile;
import org.bitbuckets.lib.tune.IValueTuner;

public class ProfiledPIDFCalculator implements Runnable, IPIDCalculator {

    final ProfiledPIDController profiledPIDController;
    final IValueTuner<double[]> tuner;

    public ProfiledPIDFCalculator(ProfiledPIDController profiledPIDController, IValueTuner<double[]> tuner) {
        this.profiledPIDController = profiledPIDController;
        this.tuner = tuner;
    }

    double cachedF = 0;

    @Override
    public void run() {


        if (tuner.hasUpdated()) {
            double[] out = tuner.consumeValue();

            profiledPIDController.setPID(out[0], out[1], out[2]);
            profiledPIDController.setConstraints(new TrapezoidProfile.Constraints(out[3], out[4]));
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
