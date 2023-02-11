package org.bitbuckets.lib.hardware;

import java.util.Optional;

public class MotorConfig {

    public final double encoderToMechanismCoefficient;
    public final double timeCoefficient;
    public final double rotationToMeterCoefficient;
    public final boolean isInverted;
    public final boolean shouldBreakOnNoCommand;
    public final double currentLimit;

    public final boolean isForwardLimitEnabled;
    public final boolean isBackwardLimitEnabled;

    @Deprecated
    public final Optional<Integer> following;

    /**
     * @param encoderToMechanismCoefficient
     * @param timeCoefficient
     * @param rotationToMeterCoefficient See {@link IEncoder}
     * @param isInverted                 whether motor is inverted
     * @param shouldBreakOnNoCommand     true if the motor should resist change when not commanded, false if not
     * @param currentLimit               limit of current
     * @param isForwardLimitEnabled
     * @param isBackwardLimitEnabled
     * @param following                  possible CAN id to follow.
     */
    public MotorConfig(double encoderToMechanismCoefficient, double timeCoefficient, double rotationToMeterCoefficient, boolean isInverted, boolean shouldBreakOnNoCommand, double currentLimit, boolean isForwardLimitEnabled, boolean isBackwardLimitEnabled, @Deprecated Optional<Integer> following) {
        this.encoderToMechanismCoefficient = encoderToMechanismCoefficient;
        this.timeCoefficient = timeCoefficient;
        this.rotationToMeterCoefficient = rotationToMeterCoefficient;
        this.isInverted = isInverted;
        this.shouldBreakOnNoCommand = shouldBreakOnNoCommand;
        this.currentLimit = currentLimit;
        this.isForwardLimitEnabled = isForwardLimitEnabled;
        this.isBackwardLimitEnabled = isBackwardLimitEnabled;
        this.following = following;
    }

}
