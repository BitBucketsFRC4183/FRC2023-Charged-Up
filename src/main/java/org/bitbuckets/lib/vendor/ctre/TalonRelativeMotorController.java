package org.bitbuckets.lib.vendor.ctre;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;
import org.bitbuckets.lib.hardware.IMotorController;

public class TalonRelativeMotorController implements IMotorController, Runnable {
    final WPI_TalonFX motor;

    TalonRelativeMotorController(WPI_TalonFX motor) {
        this.motor = motor;
    }

    @Override
    public double getMechanismFactor() {
        return 0;
    }

    @Override
    public double getRotationsToMetersFactor() {
        return 0;
    }

    @Override
    public double getRawToRotationsFactor() {
        return 0;
    }

    @Override
    public double getTimeFactor() {
        return 0;
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
    public void moveAtPercent(double percent) {
        motor.set(ControlMode.PercentOutput, percent);
    }

    @Override
    public void moveToPosition(double position_encoderRotations) {
        motor.set(ControlMode.Position, position_encoderRotations);
    }

    @Override
    public void moveAtVelocity(double velocity_encoderMetersPerSecond) {
        motor.set(ControlMode.Velocity, velocity_encoderMetersPerSecond);
    }

    @Override
    public double getSetpoint_rawUnits() {
        return motor.getClosedLoopTarget();
    }

    @Override
    public <T> T rawAccess(Class<T> clazz) throws UnsupportedOperationException {
        return clazz.cast(motor);
    }


    @Override
    public void run() {

    }
}
