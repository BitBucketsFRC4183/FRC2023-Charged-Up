package org.bitbuckets.lib.control;

import edu.wpi.first.math.controller.PIDController;
import org.bitbuckets.lib.DontUse;
import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.ProcessPath;
import org.bitbuckets.lib.tune.IValueTuner;
import org.bitbuckets.lib.log.LoggingConstants;

@DontUse @Deprecated
public class PIDCalculatorSetup implements ISetup<IPIDCalculator> {
    @Override
    public IPIDCalculator build(ProcessPath path) {
        IValueTuner<Double> p = path.generateValueTuner("p", 0.0);
        IValueTuner<Double> i = path.generateValueTuner("i", 0.0);
        IValueTuner<Double> d = path.generateValueTuner("d", 0.0);

        PIDCalculator pid = new PIDCalculator(new PIDController(0,0,0), p,i,d);
        path.registerLoop(pid, LoggingConstants.TUNING_PERIOD, "tuning-loop");

        return pid;
    }
}
