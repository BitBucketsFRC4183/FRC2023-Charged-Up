package org.bitbuckets.lib.vendor.spark;

import com.revrobotics.*;
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

    double cachedVoltage = 0;

    @Override
    public void moveAtVoltage(double voltage) {

        cachedVoltage = voltage;
        lastControlMode = LastControlMode.VOLTAGE;
        sparkMax.setVoltage(voltage);
    }

    double cachedPercent = 0;

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
    public double getVoltage() {

        throw new UnsupportedOperationException(); //what is Resistance?
        //need state space model to output
    }

    @Override
    public double getCurrent() {
        return sparkMax.getOutputCurrent();
    }

    @Override
    public double getAbsoluteEncoder_rotations() {
        return sparkMax.getAbsoluteEncoder(SparkMaxAbsoluteEncoder.Type.kDutyCycle).getPosition();
    }

    @Override
    public boolean isForwardLimitSwitchPressed() {
        return sparkMax.getForwardLimitSwitch(SparkMaxLimitSwitch.Type.kNormallyOpen).isPressed();
    }

    @Override
    public boolean isReverseLimitSwitchPressed() {
        return sparkMax.getReverseLimitSwitch(SparkMaxLimitSwitch.Type.kNormallyOpen).isPressed();
    }

    boolean isLimp = false;

    @Override
    public void goLimp() {
        this.moveAtVoltage(0);
    }

    @Override
    public <T> T rawAccess(Class<T> clazz) throws UnsupportedOperationException {
        return clazz.cast(sparkMax);
    }



}
