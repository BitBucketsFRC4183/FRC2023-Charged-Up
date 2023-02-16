package org.bitbuckets.arm.sim;

public class ArmConfig {

    public final double lengthMeters;
    public final double armMass;
    public final double armMaxAngle;
    public final double armMinAngle;
    public final boolean isGravitySimulated;

    public ArmConfig(double lengthMeters, double armMass, double armMaxAngle, double armMinAngle, boolean isGravitySimulated) {
        this.lengthMeters = lengthMeters;
        this.armMass = armMass;
        this.armMaxAngle = armMaxAngle;
        this.armMinAngle = armMinAngle;
        this.isGravitySimulated = isGravitySimulated;
    }
}
