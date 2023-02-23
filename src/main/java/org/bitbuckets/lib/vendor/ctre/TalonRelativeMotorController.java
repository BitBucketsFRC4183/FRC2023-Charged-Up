package org.bitbuckets.lib.vendor.ctre;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;
import org.bitbuckets.lib.hardware.IMotorController;
import org.bitbuckets.lib.hardware.MotorConfig;

@Deprecated
public class TalonRelativeMotorController implements IMotorController, Runnable {
    final WPI_TalonFX motor;
    final MotorConfig motorConfig;

    TalonRelativeMotorController(WPI_TalonFX motor, MotorConfig motorConfig) {
        this.motor = motor;
        this.motorConfig = motorConfig;
    }

    @Override
    public double getMechanismFactor() {
        return motorConfig.encoderToMechanismCoefficient;
    }

    @Override
    public double getRotationsToMetersFactor() {
        return motorConfig.rotationToMeterCoefficient;
    }

    @Override
    public double getRawToRotationsFactor() {
        return 1 / 2048.; //Sparks units are in rotations
    }

    @Override
    public double getTimeFactor() {
        return motorConfig.timeCoefficient;
    }

    @Override
    public double getPositionRaw() {
        return motor.getSelectedSensorPosition();
    }

    @Override
    public double getVelocityRaw() {
        return motor.getSelectedSensorVelocity();
    }

    @Override
    public void forceOffset(double offsetUnits_baseUnits) {
        motor.setSelectedSensorPosition(offsetUnits_baseUnits);
    }

    @Override
    public void forceOffset_mechanismRotations(double offsetUnits_mechanismRotations) {
        double mechanismToEncoderCoefficient = (1.0 / motorConfig.encoderToMechanismCoefficient);
        double offsetUnits_encoderRotations = offsetUnits_mechanismRotations * mechanismToEncoderCoefficient;

        forceOffset(offsetUnits_encoderRotations);
    }

    @Override
    public void moveAtVoltage(double voltage) {
        motor.setVoltage(voltage);
    }

    @Override
    public void moveAtPercent(double percent) {
        motor.set(ControlMode.PercentOutput, percent);
    }

    @Override
    public void moveToPosition(double position_encoderRotations) {
        motor.set(ControlMode.Position, position_encoderRotations);
    }

    @Override
    public void moveToPosition_mechanismRotations(double position_mechanismRotations) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void moveAtVelocity(double velocity_encoderMetersPerSecond) {
        motor.set(ControlMode.Velocity, velocity_encoderMetersPerSecond);
    }

    @Override
    public double getSetpoint_mechanismRotations() {
        return motor.getClosedLoopTarget();
    }

    @Override
    public double getVoltage() {
        return motor.getMotorOutputVoltage();
    }

    @Override
    public double getCurrent() {
        return motor.getStatorCurrent();
    }

    @Override
    public <T> T rawAccess(Class<T> clazz) throws UnsupportedOperationException {
        return clazz.cast(motor);
    }


    @Override
    public void run() {

    }
}
