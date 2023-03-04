package org.bitbuckets.lib.vendor.ctre;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;
import org.bitbuckets.lib.hardware.IMotorController;
import org.bitbuckets.lib.hardware.MotorConfig;

@Deprecated
public class TalonRelativeMotorController implements IMotorController, Runnable {
    final WPI_TalonFX motor;
    final MotorConfig motorConfig;

    ControlMode lastControlMode = ControlMode.PercentOutput;

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


    // We are only using talons for drive on appa, but we use voltage comp for drive, so this
    // throws a bunch of errors
    @Override
    public void moveAtVoltage(double voltage) {
        motor.set(ControlMode.PercentOutput, voltage / 12);
        lastControlMode = ControlMode.PercentOutput;
    }

    @Override
    public void moveAtPercent(double percent) {
        motor.set(ControlMode.PercentOutput, percent);
        lastControlMode = ControlMode.PercentOutput;

    }

    @Override
    public void moveToPosition(double position_encoderRotations) {
        motor.set(ControlMode.Position, position_encoderRotations);
        lastControlMode = ControlMode.Position;
    }

    @Override
    public void moveToPosition_mechanismRotations(double position_mechanismRotations) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void moveAtVelocity(double velocity_encoderMetersPerSecond) {
        motor.set(ControlMode.Velocity, velocity_encoderMetersPerSecond);
        lastControlMode = ControlMode.Velocity;
    }

    @Override
    public double getSetpoint_mechanismRotations() {
        if (lastControlMode == ControlMode.Position || lastControlMode == ControlMode.MotionMagic) {
            return motor.getClosedLoopTarget();
        } else {
            return 0;
        }
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
    public void goLimp() {
        throw new UnsupportedOperationException(); //no
    }

    @Override
    public <T> T rawAccess(Class<T> clazz) throws UnsupportedOperationException {
        return clazz.cast(motor);
    }


    @Override
    public void run() {

    }
}
