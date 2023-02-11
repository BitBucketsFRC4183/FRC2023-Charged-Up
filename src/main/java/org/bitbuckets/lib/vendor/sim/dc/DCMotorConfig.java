package org.bitbuckets.lib.vendor.sim.dc;

import edu.wpi.first.math.Matrix;
import edu.wpi.first.math.numbers.N1;
import edu.wpi.first.math.numbers.N2;

public class DCMotorConfig {

    public final double momentOfInertia;
    public final Matrix<N1, N1> stdDevs;

    public DCMotorConfig(double momentOfInertia, Matrix<N1, N1> stdDevs) {
        this.momentOfInertia = momentOfInertia;
        this.stdDevs = stdDevs;
    }
}
