package org.bitbuckets.lib.hardware;

/**
 * Can be obtained in the same place as IMotor since most IMotor setups are producing IMotorControllers
 */
public interface IMotorController extends IMotor, IEncoder {


    /**
     * Gets error in terms of mechanism rotations
     * @return the current error
     */
    default double getError_mechanismRotations(){
        double setpoint = getSetpoint_mechanismRotations();
        double actualPosition = getMechanismPositionAccum_rot();
        return Math.abs(setpoint - actualPosition);
    }

}
