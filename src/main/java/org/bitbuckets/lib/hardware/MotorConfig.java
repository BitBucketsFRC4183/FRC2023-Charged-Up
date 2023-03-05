package org.bitbuckets.lib.hardware;

import edu.wpi.first.math.system.plant.DCMotor;

import java.util.Optional;

public class MotorConfig {

    //aparently this isnt the same as gearing LMAO, this is 1/gearing*othercoefficients like chains
    //blame my shitty understanding of physix
    public final double encoderToMechanismCoefficient;
    public final double timeCoefficient;
    public final double rotationToMeterCoefficient;

    //This is ignored if in follower mode because... sparks
    public final boolean isInverted;
    public final boolean shouldBreakOnNoCommand;
    public final double currentLimit;

    public final Optional<Double> forwardSoftLimitMechanismAccum_rot;
    public final Optional<Double> reverseSoftLimitMechanismAccum_rot;

    public final boolean isForwardHardLimitEnabled;
    public final boolean isBackwardHardLimitEnabled;

    public final boolean isRampRateEnabled;

    public final OptimizationMode optimizationMode;
    public final DCMotor motorType;

    /**
     * @param encoderToMechanismCoefficient
     * @param timeCoefficient
     * @param rotationToMeterCoefficient         See {@link IEncoder}
     * @param isInverted                         whether motor is inverted
     * @param shouldBreakOnNoCommand             true if the motor should resist change when not commanded, false if not
     * @param currentLimit                       limit of current
     * @param forwardSoftLimitMechanismAccum_rot
     * @param reverseSoftLimitMechanismAccum_rot
     * @param isForwardHardLimitEnabled
     * @param isBackwardHardLimitEnabled
     * @param isRampRateEnabled
     * @param optimizationMode
     * @param motorType
     */
    public MotorConfig(double encoderToMechanismCoefficient, double timeCoefficient, double rotationToMeterCoefficient, boolean isInverted, boolean shouldBreakOnNoCommand, double currentLimit, Optional<Double> forwardSoftLimitMechanismAccum_rot, Optional<Double> reverseSoftLimitMechanismAccum_rot, boolean isForwardHardLimitEnabled, boolean isBackwardHardLimitEnabled, boolean isRampRateEnabled, OptimizationMode optimizationMode, DCMotor motorType) {
        this.encoderToMechanismCoefficient = encoderToMechanismCoefficient;
        this.timeCoefficient = timeCoefficient;
        this.rotationToMeterCoefficient = rotationToMeterCoefficient;
        this.isInverted = isInverted;
        this.shouldBreakOnNoCommand = shouldBreakOnNoCommand;
        this.currentLimit = currentLimit;
        this.forwardSoftLimitMechanismAccum_rot = forwardSoftLimitMechanismAccum_rot;
        this.reverseSoftLimitMechanismAccum_rot = reverseSoftLimitMechanismAccum_rot;
        this.isForwardHardLimitEnabled = isForwardHardLimitEnabled;
        this.isBackwardHardLimitEnabled = isBackwardHardLimitEnabled;
        this.isRampRateEnabled = isRampRateEnabled;
        this.optimizationMode = optimizationMode;
        this.motorType = motorType;
    }

}
