package org.bitbuckets.lib.vendor.sim.elevator;

import edu.wpi.first.math.Matrix;
import edu.wpi.first.math.numbers.N1;

public class ElevatorConfig {

    public final double carriageMassKg;
    public final double drumRadius;
    public final double minHeight;
    public final double maxHeight;
    public final Matrix<N1, N1> stdDevs;

    public ElevatorConfig(double carriageMassKg, double drumRadius, double minHeight, double maxHeight, Matrix<N1, N1> stdDevs) {
        this.carriageMassKg = carriageMassKg;
        this.drumRadius = drumRadius;
        this.minHeight = minHeight;
        this.maxHeight = maxHeight;
        this.stdDevs = stdDevs;
    }
}
