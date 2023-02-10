package org.bitbuckets.lib.vendor.spark;

import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.SparkMaxPIDController;
import org.bitbuckets.lib.hardware.IMotorController;
import org.bitbuckets.lib.hardware.MotorConfig;

public class SparkRelativeMotorController implements IMotorController {


    final MotorConfig motorConfig;
    final CANSparkMax sparkMax;
    final RelativeEncoder sparkMaxRelativeEncoder;
    final SparkMaxPIDController sparkMaxPIDController;

    SparkRelativeMotorController(MotorConfig motorConfig, CANSparkMax sparkMax) {
        this.motorConfig = motorConfig;
        this.sparkMax = sparkMax;
        this.sparkMaxPIDController = sparkMax.getPIDController();
        this.sparkMaxRelativeEncoder = sparkMax.getEncoder();
    }

    LastControlMode lastControlMode = LastControlMode.NONE;
    double lastSetpoint_mechanismRotations = 0;


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
        return 1; //Sparks units are in rotations
    }

    @Override
    public double getTimeFactor() {
        return motorConfig.timeCoefficient;
    }

    @Override
    public double getPositionRaw() {
        return sparkMaxRelativeEncoder.getPosition();
    }

    @Override
    public double getVelocityRaw() {
        return sparkMaxRelativeEncoder.getVelocity();
    }

    @Override
    public void forceOffset(double offsetUnits_baseUnits) {
        sparkMax.getEncoder().setPosition(offsetUnits_baseUnits);
    }

    @Override
    public void forceOffset_mechanismRotations(double offsetUnits_mechanismRotations) {

        double mechanismToEncoderCoefficient = (1.0 / motorConfig.encoderToMechanismCoefficient);
        double offsetUnits_encoderRotations = offsetUnits_mechanismRotations * mechanismToEncoderCoefficient;

        sparkMaxRelativeEncoder.setPosition(offsetUnits_encoderRotations);

    }

    @Override
    public void moveAtVoltage(double voltage) {
        lastControlMode = LastControlMode.VOLTAGE;
        sparkMax.setVoltage(voltage);
    }

    @Override
    public void moveAtPercent(double percent) {
        lastControlMode = LastControlMode.PERCENT;
        sparkMax.set(percent);
    }



    @Override
    public void moveToPosition(double position_encoderRotations) {
        lastControlMode = LastControlMode.POSITION;
        lastSetpoint_mechanismRotations = position_encoderRotations * motorConfig.encoderToMechanismCoefficient;

        sparkMaxPIDController.setReference(position_encoderRotations, CANSparkMax.ControlType.kPosition);
    }

    @Override
    public void moveToPosition_mechanismRotations(double position_mechanismRotations) {
        lastControlMode = LastControlMode.POSITION;
        lastSetpoint_mechanismRotations = position_mechanismRotations;
        double position_encoderRotations = position_mechanismRotations * (1.0 / motorConfig.encoderToMechanismCoefficient);

        sparkMaxPIDController.setReference(position_encoderRotations, CANSparkMax.ControlType.kPosition);
    }

    @Override
    public void moveAtVelocity(double velocity_encoderMetersPerSecond) {
        lastControlMode = LastControlMode.VELOCITY;
        double rotationsPerMinute = velocity_encoderMetersPerSecond / getRotationsToMetersFactor() * 60.0;

        sparkMaxPIDController.setReference(rotationsPerMinute, CANSparkMax.ControlType.kVelocity);
    }

    @Override
    public double getSetpoint_mechanismRotations() {
        return lastSetpoint_mechanismRotations;
    }

    @Override
    public <T> T rawAccess(Class<T> clazz) throws UnsupportedOperationException {
        return clazz.cast(sparkMax);
    }

}
