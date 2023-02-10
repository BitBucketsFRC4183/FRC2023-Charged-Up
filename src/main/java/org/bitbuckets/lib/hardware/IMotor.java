package org.bitbuckets.lib.hardware;

/**
 * Represents setpoint control over a device, make sure you know what kind of units you
 * configured it to use, because a velocity pid setpoint is much different than controlling velocity via voltage
 * <p>
 * sorry if this is confusing it's basically just motor.set() but with checks to make it safer and
 * also let you use many  types of motors and not cry
 * <p>
 * Common ways of obtaining: TalonPositionSetup, TalonVelocitySetup, TalonPercentSetup
 */
public interface IMotor extends IRaw {

    /**
     *
     * @param voltage voltage to move at
     */
    void moveAtVoltage(double voltage);


    /**
     * This method will tell the motor controller underneath to move at a specified percent of
     * its max speed. Cannot be used in conjunction with {@link #moveToPosition(double)} or {@link #moveAtVelocity(double)}
     *
     * @param percent -1.0 to 1.0 representing a percentage of speed
     */
    void moveAtPercent(double percent);

    /**
     * commands to move using pid position control to a specified position of units rotations.
     * To tune the pid constants of this device please use the shuffleboard
     * @param position_encoderRotations
     */
    @Deprecated
    void moveToPosition(double position_encoderRotations);

    /**
     * Commands to move using pid position in rotations of the mechanism. Does not wrap around.
     * @param position_mechanismRotations
     */
    void moveToPosition_mechanismRotations(double position_mechanismRotations);

    /**
     * commands to move with pid control at a velocity
     * should throw if not pid
     *
     * @param velocity_encoderMetersPerSecond
     */
    void moveAtVelocity(double velocity_encoderMetersPerSecond);

    /**
     *
     * Gets the current desired position of the pid algorithm in the units used by the controller
     * @return The current PID setpoint, if using PID, in the raw units of the motor controller
     */
    double getSetpoint_rawUnits();


}
