package org.bitbuckets.lib.hardware;

/**
 * Can be obtained in the same place as IMotor since most IMotor setups are producing IMotorControllers
 */
public interface IMotorController extends IMotor, IEncoder {

    /**
     * Gets the current setpoint of the pid algorithm, converted to rotations
     * @return the current setpoint
     */
    default double getEncoderSetpointAccumulated_rotations() {
        return getSetpoint_rawUnits() * getRawToRotationsFactor();
    }

    /**
     * Gets error in terms of encoder rotations
     * @return the current error
     */
    default double getError(){
        double setpoint = getSetpoint_rawUnits();
        double actualPosition = getPositionRaw();
        return setpoint - actualPosition;
    }

}
