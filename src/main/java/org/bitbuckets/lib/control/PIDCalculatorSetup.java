package org.bitbuckets.lib.control;

import edu.wpi.first.math.controller.PIDController;
import org.bitbuckets.lib.IProcess;
import org.bitbuckets.lib.ISetup;
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
        IValueTuner<Double> p = self.generateTuner(Double.class, "p", pidConfig.kP);
        IValueTuner<Double> i = self.generateTuner(Double.class, "i", pidConfig.kI);
        IValueTuner<Double> d = self.generateTuner(Double.class, "d", pidConfig.kD);

        double pVal = p.readValue();
        double iVal = i.readValue();
        double dVal = d.readValue();

        return new PIDCalculator(new PIDController(pVal, iVal, dVal), p,i,d);
    }
}
