package org.bitbuckets.lib.hardware;

/**
 * Can be obtained in the same place as IMotor since most IMotor setups are producing IMotorControllers
 */
public interface IMotorController extends IMotor, IEncoder {

    default double getEncoderSetpointAccumulated_rotations() {
        return getSetpoint_rawUnits() * getRawToRotationsFactor();
    }

}
