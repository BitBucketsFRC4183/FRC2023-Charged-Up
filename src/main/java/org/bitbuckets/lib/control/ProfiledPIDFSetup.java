package org.bitbuckets.lib.control;

import edu.wpi.first.math.trajectory.TrapezoidProfile;
import org.bitbuckets.lib.IProcess;
import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.tune.IValueTuner;

public class ProfiledPIDFSetup implements ISetup<IPIDCalculator> {

    final PIDConfig pidConfig;
    final ProfileConfig profileConfig;

    public ProfiledPIDFSetup(PIDConfig pidConfig, ProfileConfig profileConfig) {
        this.pidConfig = pidConfig;
        this.profileConfig = profileConfig;
    }

    @Override
    public IPIDCalculator build(IProcess self) {

        IValueTuner<Double> pTune = self.generateTuner(Double.class, "p", pidConfig.kP);
        IValueTuner<Double> iTune = self.generateTuner(Double.class, "i", pidConfig.kI);
        IValueTuner<Double> dTune = self.generateTuner(Double.class, "d", pidConfig.kD);
        IValueTuner<Double> kVTune = self.generateTuner(Double.class, "kV", profileConfig.kV);
        IValueTuner<Double> kATune = self.generateTuner(Double.class, "kA", profileConfig.kA);

        double p = pTune.readValue();
        double i = iTune.readValue();
        double d = dTune.readValue();
        double kV = kVTune.readValue();
        double kA = kATune.readValue();

        ProfiledPIDFController controller = new ProfiledPIDFController(p,i,d,0, new TrapezoidProfile.Constraints(kV, kA));
        return new ProfiledPIDFCalculator(controller, pTune, iTune, dTune, kVTune, kATune);
    }
}
