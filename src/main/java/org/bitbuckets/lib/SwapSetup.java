package org.bitbuckets.lib;

public class SwapSetup<T> implements ISetup<T> {

    //do stuff based on the robot

    final ISetup<T> useOnAppa;
    final ISetup<T> useOnNew;
    final ISetup<T> useOnSim;

    public SwapSetup(ISetup<T> useOnAppa, ISetup<T> useOnNew, ISetup<T> useOnSim) {
        this.useOnAppa = useOnAppa;
        this.useOnNew = useOnNew;
        this.useOnSim = useOnSim;
    }

    @Override
    public T build(IProcess self) {
        if (self.isReal()) {
            return useOnNew.build(self);
        } else {
            return useOnSim.build(self);
        }
    }
}
