package org.bitbuckets.lib.vendor.sim.dc;

import edu.wpi.first.math.Matrix;
import edu.wpi.first.math.numbers.N1;

/**
 * Represents the physics of a dc motor
 */
public class SimInertiaConfig {

    public final double momentOfInertia;
    public final Matrix<N1, N1> stdDevs;

    public SimInertiaConfig(double momentOfInertia, Matrix<N1, N1> stdDevs) {
        this.momentOfInertia = momentOfInertia;
        this.stdDevs = stdDevs;
    }
}
