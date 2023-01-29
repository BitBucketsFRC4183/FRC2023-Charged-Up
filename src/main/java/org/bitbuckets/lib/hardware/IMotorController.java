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

}
