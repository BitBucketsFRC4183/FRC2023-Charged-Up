package org.bitbuckets.lib.hardware;

public class MotorConstants {

    public final double mechanismCoefficient;
    public final double timeCoefficient;
    public final double rotationToMeterCoefficient;
    public final boolean isInverted;
    public final boolean shouldBreakOnNoCommand;
    public final double currentLimit;

    public MotorConstants(double mechanismCoefficient, double timeCoefficient, double rotationToMeterCoefficient, boolean isInverted, boolean shouldBreakOnNoCommand, double currentLimit) {
        this.mechanismCoefficient = mechanismCoefficient;
        this.timeCoefficient = timeCoefficient;
        this.rotationToMeterCoefficient = rotationToMeterCoefficient;
        this.isInverted = isInverted;
        this.shouldBreakOnNoCommand = shouldBreakOnNoCommand;
        this.currentLimit = currentLimit;
    }
}
