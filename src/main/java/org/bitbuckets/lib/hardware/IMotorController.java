package org.bitbuckets.lib.hardware;

/**
 * Can be obtained in the same place as IMotor since most IMotor setups are producing IMotorControllers
 */
public interface IMotorController extends IMotor, IEncoder {


    /**
     * Gets error in terms of encoder rotations
     * @return the current error
     */
    @Deprecated
    default double getError(){
        double setpoint = getSetpoint_rawUnits();
        double actualPosition = getPositionRaw();
        return setpoint - actualPosition;
    }

}
