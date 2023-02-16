package org.bitbuckets.lib.control;

/**
 * Configuration for a PID controller with feedforward
 */
public class PIDConfig {

    public final double kP;
    public final double kI;
    public final double kD;
    @Deprecated //isn't used currently
    public final double kF;

    /**
     * Use these when working with arrays
     */
    public static final byte P = 0;
    public static final byte I = 1;
    public static final byte D = 2;
    public static final byte F = 3;

    /**
     *
     * @param kP proportional constant for a PID controller, all you need
     * @param kI dont use this (integral constant, makes your pid controller unstable and suffer from integral windup)
     * @param kD derivative constant for a PID controller
     * @param kF feedforward constant for a PID controller, use to correct steady state error
     */
    public PIDConfig(double kP, double kI, double kD, @Deprecated double kF) {
        this.kP = kP;
        this.kI = kI;
        this.kD = kD;
        this.kF = kF;
    }
}
