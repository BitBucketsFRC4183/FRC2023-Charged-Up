package org.bitbuckets.lib;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class SwitcherSetup<T> implements ISetup<T> {

    //do stuff based on the robot

    final ISetup<T> useOnAppa;
    final ISetup<T> useOnNew;
    final ISetup<T> useOnSim;

    public SwitcherSetup(ISetup<T> useOnAppa, ISetup<T> useOnNew, ISetup<T> useOnSim) {
        this.useOnAppa = useOnAppa;
        this.useOnNew = useOnNew;
        this.useOnSim = useOnSim;
    }

    public SwitcherSetup(ISetup<T> useOnNew, ISetup<T> sim) {
        this.useOnAppa = null;
        this.useOnNew = useOnNew;
        this.useOnSim = sim;
    }

    @Override
    public T build(IProcess self) {
        if (self.isReal()) {
            try {
                String localhost = InetAddress.getLocalHost().getCanonicalHostName();
            } catch (UnknownHostException e) {
                throw new RuntimeException(e);
            }

            return useOnAppa.build(self);
        } else {
            return useOnSim.build(self);
        }
    }
}
