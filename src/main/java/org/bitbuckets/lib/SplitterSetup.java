package org.bitbuckets.lib;

public class SplitterSetup<T> implements ISetup<T> {

    //do stuff based on the robot

    final ISetup<T> real;
    final ISetup<T> simulated;

    public SplitterSetup(ISetup<T> real, ISetup<T> simulated) {
        this.real = real;
        this.simulated = simulated;
    }

    @Override
    public T build(ProcessPath self) {
        if (self.isReal()) {
            return real.build(self);
        } else {
            return simulated.build(self);
        }
    }
}
