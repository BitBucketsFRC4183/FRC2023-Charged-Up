package org.bitbuckets.lib.control;

import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.trajectory.TrapezoidProfile;
import org.bitbuckets.lib.ILogAs;
import org.bitbuckets.lib.IProcess;
import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.ITuneAs;

public class ProfiledPIDFCalculatorSetup implements ISetup<IPIDCalculator> {

    final PIDConfig pidConfig;
    final TrapezoidProfile.Constraints profile;

    public ProfiledPIDFCalculatorSetup(PIDConfig pidConfig, TrapezoidProfile.Constraints profile) {
        this.pidConfig = pidConfig;
        this.profile = profile;
    }

    @Override
    public IPIDCalculator build(IProcess self) {
        return new ProfiledPIDCalculator(
                new ProfiledPIDController(pidConfig.kP, pidConfig.kI, pidConfig.kD, profile),
                self.generateTuner(ITuneAs.DOUBLE_INPUT, "proportional", pidConfig.kP),
                self.generateTuner(ITuneAs.DOUBLE_INPUT, "integral", pidConfig.kI),
                self.generateTuner(ITuneAs.DOUBLE_INPUT, "derivative", pidConfig.kD),
                self.generateTuner(ITuneAs.DOUBLE_INPUT, "max-velocity", profile.maxVelocity),
                self.generateTuner(ITuneAs.DOUBLE_INPUT, "max-accel", profile.maxAcceleration),
                self.generateLogger(ILogAs.DOUBLE, "last-setpoint"),
                self.generateLogger(ILogAs.DOUBLE, "last-actual"),
                self.generateLogger(ILogAs.DOUBLE, "last-output")

        );
    }
}
