package org.bitbuckets.lib.control;

import edu.wpi.first.math.controller.ProfiledPIDController;
import org.bitbuckets.lib.tune.IValueTuner;

public class ProfiledPIDCalculator implements Runnable {

    final ProfiledPIDController profiledPIDController;
    final IValueTuner<double[]> tuner;

    public ProfiledPIDCalculator(ProfiledPIDController profiledPIDController, IValueTuner<double[]> tuner) {
        this.profiledPIDController = profiledPIDController;
        this.tuner = tuner;
    }

    @Override
    public void run() {

    }
}
