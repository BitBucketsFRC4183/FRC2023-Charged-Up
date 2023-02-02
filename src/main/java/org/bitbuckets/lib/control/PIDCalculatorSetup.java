package org.bitbuckets.lib.control;

import edu.wpi.first.math.controller.PIDController;
import org.bitbuckets.lib.DontUseIncubating;
import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.ProcessPath;
import org.bitbuckets.lib.log.LoggingConstants;
import org.bitbuckets.lib.tune.IValueTuner;

@DontUseIncubating
@Deprecated
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
    public IPIDCalculator build(ProcessPath path) {
        IValueTuner<double[]> pidf = path.generateMultiTuner(
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

        path.registerLoop(pid, LoggingConstants.TUNING_PERIOD, "tuning-loop");

        return pid;
    }
}
