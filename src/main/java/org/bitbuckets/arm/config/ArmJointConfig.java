package org.bitbuckets.arm.config;

import edu.wpi.first.math.system.plant.DCMotor;

public class ArmJointConfig {

    public final double length_meters;
    public final double jointMass_kg;
    public final double jointMaxAngle_rads;
    public final double jointMinAngle_rads;
    public final double angularInertia_kgm2; //same thing as moi
    public final double cgRadius_meters; //radius from the center of gravity to the point being considered to find moi
    public final boolean isGravitySimulated;
    public final DCMotor gearbox;//suffer

    public ArmJointConfig(double length_meters, double jointMass_kg, double jointMaxAngle_rads, double jointMinAngle_rads, double angularInertia_kgm2, double cgRadius_meters, boolean isGravitySimulated, DCMotor gearbox) {
        this.length_meters = length_meters;
        this.jointMass_kg = jointMass_kg;
        this.jointMaxAngle_rads = jointMaxAngle_rads;
        this.jointMinAngle_rads = jointMinAngle_rads;
        this.angularInertia_kgm2 = angularInertia_kgm2;
        this.cgRadius_meters = cgRadius_meters;
        this.isGravitySimulated = isGravitySimulated;
        this.gearbox = gearbox;
    }
}
