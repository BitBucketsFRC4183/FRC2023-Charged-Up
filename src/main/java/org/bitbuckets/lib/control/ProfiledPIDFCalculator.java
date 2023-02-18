package org.bitbuckets.lib.control;

import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.trajectory.TrapezoidProfile;
import org.bitbuckets.lib.log.Debuggable;
import org.bitbuckets.lib.tune.IValueTuner;

public class ProfiledPIDFCalculator implements Runnable, IPIDCalculator {

    final ProfiledPIDController profiledPIDController;
    final IValueTuner<double[]> tuner;
    final Debuggable debuggable;

    public ProfiledPIDFCalculator(ProfiledPIDController profiledPIDController, IValueTuner<double[]> tuner, Debuggable debuggable) {
        this.profiledPIDController = profiledPIDController;
        this.tuner = tuner;
        this.debuggable = debuggable;
    }

    double cachedF = 0;

    @Override
    public void run() {


        if (tuner.hasUpdated()) {
            double[] out = tuner.consumeValue();

            profiledPIDController.setPID(out[0],
                    out[1],
                    out[2]);
            profiledPIDController.setConstraints(new TrapezoidProfile.Constraints(out[3], out[4]));
        }


        debuggable.log("pos-setpoint", profiledPIDController.getSetpoint().position);
        debuggable.log("pos-error", profiledPIDController.getPositionError());
        debuggable.log("goal",profiledPIDController.getGoal().toString());
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
