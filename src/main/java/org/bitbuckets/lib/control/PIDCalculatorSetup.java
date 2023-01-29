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

    final double firstP;
    final double firstI;
    final double firstD;

    public PIDCalculatorSetup(double firstP, double firstI, double firstD) {
        this.firstP = firstP;
        this.firstI = firstI;
        this.firstD = firstD;
    }

    @Override
    public IPIDCalculator build(ProcessPath path) {
        IValueTuner<Double> p = path.generateValueTuner("p", firstP);
        IValueTuner<Double> i = path.generateValueTuner("i", firstI);
        IValueTuner<Double> d = path.generateValueTuner("d", firstD);

        PIDCalculator pid = new PIDCalculator(new PIDController(0,0,0), p,i,d);
        path.registerLoop(pid, LoggingConstants.TUNING_PERIOD, "tuning-loop");

        return pid;
    }
}
