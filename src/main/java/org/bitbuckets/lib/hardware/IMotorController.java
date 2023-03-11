package org.bitbuckets.lib.hardware;

/**
 * Can be obtained in the same place as IMotor since most IMotor setups are producing IMotorControllers
 */
public interface IMotorController extends IMotor, IEncoder {

    default double getError_mechanismRotations() {
        double setpoint = getSetpoint_mechanismRotations();
        double actualPosition = getMechanismPositionAccum_rot();
        return setpoint - actualPosition;
    }

    default boolean isForwardLimitSwitchPressed() {
        return false;
    }

    default boolean isReverseLimitSwitchPressed() {
        return false;
    }

}
