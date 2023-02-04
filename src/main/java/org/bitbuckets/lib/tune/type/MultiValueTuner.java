package org.bitbuckets.lib.tune.type;

import org.bitbuckets.lib.tune.IValueTuner;

public class MultiValueTuner implements IValueTuner<double[]> {

    final IValueTuner<Double>[] tunners;

    public MultiValueTuner(IValueTuner<Double>[] tunners) {
        this.tunners = tunners;
    }

    @Override
    public double[] readValue() {
        double[] doubles = new double[tunners.length];

        for (int i = 0; i < tunners.length; i++) {
            doubles[i] = tunners[i].readValue();
        }

        return doubles;
    }

    @Override
    public double[] consumeValue() {
        double[] doubles = new double[tunners.length];

        for (int i = 0; i < tunners.length; i++) {
            doubles[i] = tunners[i].consumeValue();
        }

        return doubles;
    }

    @Override
    public boolean hasUpdated() {
        for (IValueTuner<Double> tuner : tunners) {
            if (tuner.hasUpdated()) {
                return true;
            }
        }
        return false;
    }

}
