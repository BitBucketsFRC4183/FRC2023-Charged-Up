package org.bitbuckets.lib.control;

import edu.wpi.first.math.controller.PIDController;
import org.bitbuckets.lib.ILogAs;
import org.bitbuckets.lib.IProcess;
import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.ITuneAs;
import org.bitbuckets.lib.tune.IValueTuner;

public class PIDCalculatorSetup implements ISetup<IPIDCalculator> {

    final PIDConfig pidConfig;

    /**
     * WARNING: currently ignores kF
     * @param pidConfig
     */
    public PIDCalculatorSetup(PIDConfig pidConfig) {
        this.pidConfig = pidConfig;
    }

    @Override
    public IPIDCalculator build(IProcess self) {

        return new PIDCalculator(
                new PIDController(pidConfig.kP, pidConfig.kI, pidConfig.kD),
                self.generateTuner(ITuneAs.DOUBLE_INPUT, "proportional", pidConfig.kP),
                self.generateTuner(ITuneAs.DOUBLE_INPUT, "integral", pidConfig.kI),
                self.generateTuner(ITuneAs.DOUBLE_INPUT, "derivative", pidConfig.kD),
                self.generateLogger(ILogAs.DOUBLE, "last-setpoint"),
                self.generateLogger(ILogAs.DOUBLE, "last-actual"),
                self.generateLogger(ILogAs.DOUBLE, "last-output")
        );
    }
}
