package org.bitbuckets.lib.control;

import edu.wpi.first.math.trajectory.TrapezoidProfile;
import org.bitbuckets.lib.IProcess;
import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.ITuneAs;
import org.bitbuckets.lib.tune.IValueTuner;

public class ProfiledPIDFSetup implements ISetup<IPIDCalculator> {

    final PIDConfig pidConfig;
    final TrapezoidProfile.Constraints profile;

    public ProfiledPIDFSetup(PIDConfig pidConfig, TrapezoidProfile.Constraints profile) {
        this.pidConfig = pidConfig;
        this.profile = profile;
    }

    @Override
    public IPIDCalculator build(IProcess self) {

        var p = self.generateTuner(ITuneAs.DOUBLE_INPUT, "p", pidConfig.kP);
        var i = self.generateTuner(ITuneAs.DOUBLE_INPUT, "i", pidConfig.kI);
        var d = self.generateTuner(ITuneAs.DOUBLE_INPUT, "d", pidConfig.kD);
        var kV = self.generateTuner(ITuneAs.DOUBLE_INPUT, "kV", profile.maxVelocity);
        var kA = self.generateTuner(ITuneAs.DOUBLE_INPUT, "kA", profile.maxAcceleration);

        ProfiledPIDFController controller = new ProfiledPIDFController(
                p.readValue(),
                i.readValue(),
                d.readValue(),
                0,
                new TrapezoidProfile.Constraints(kV.readValue(), kA.readValue())
        );

        return new ProfiledPIDFCalculator(controller, p,i,d,kV,kA);
    }
}
