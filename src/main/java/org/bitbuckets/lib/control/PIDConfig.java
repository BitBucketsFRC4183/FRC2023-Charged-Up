package org.bitbuckets.lib.control;

import java.util.Optional;

/**
 * Configuration for a PID controller with feedforward
 */
public class PIDConfig {

    public final double kP;
    public final double kI;
    public final double kD;

    public final Optional<Double> continuousMin;
    public final Optional<Double> continuousMax;

    /**
     * @param kP            proportional constant for a PID controller, all you need
     * @param kI            dont use this (integral constant, makes your pid controller unstable and suffer from integral windup)
     * @param kD            derivative constant for a PID controller
     * @param continuousMin
     * @param continuousMax
     */
    public PIDConfig(double kP, double kI, double kD, Optional<Double> continuousMin, Optional<Double> continuousMax) {
        this.kP = kP;
        this.kI = kI;
        this.kD = kD;
        this.continuousMin = continuousMin;
        this.continuousMax = continuousMax;
    }
}
