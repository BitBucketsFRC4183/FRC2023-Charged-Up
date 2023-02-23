package org.bitbuckets.lib.control;

import edu.wpi.first.math.controller.PIDController;
import org.bitbuckets.lib.DontUseIncubating;
import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.ProcessPath;
import org.bitbuckets.lib.log.LoggingConstants;
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
    public IPIDCalculator build(ProcessPath self) {
        IValueTuner<double[]> pidf = self.generateMultiTuner(
                new String[] {"p", "i", "d", "f"},
                new double[] {pidConfig.kP, pidConfig.kI, pidConfig.kD, pidConfig.kF}
        );

        double[] pidfNow = pidf.readValue();

        PIDController use = new PIDController(
                pidfNow[PIDConfig.P],
                pidfNow[PIDConfig.I],
                pidfNow[PIDConfig.D]
        );

        PIDCalculator pid = new PIDCalculator(
                use,
                pidf
        );

        self.registerLoop(pid, LoggingConstants.TUNING_PERIOD, "tuning-loop");

        return pid;
    }
}
