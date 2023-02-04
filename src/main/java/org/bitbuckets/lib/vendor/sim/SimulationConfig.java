package org.bitbuckets.lib.vendor.sim;

public class SimulationConfig {

    public final double simulatedMomentOfInertia;
    public final double simulatedP;
    public final double simulatedI;
    public final double simulatedD;

    public SimulationConfig(double simulatedMomentOfInertia, double simulatedP, double simulatedI, double simulatedD) {
        this.simulatedMomentOfInertia = simulatedMomentOfInertia;
        this.simulatedP = simulatedP;
        this.simulatedI = simulatedI;
        this.simulatedD = simulatedD;
    }

    //TODO matrix of std deviations

}
