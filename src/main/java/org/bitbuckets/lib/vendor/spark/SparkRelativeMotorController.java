package org.bitbuckets.lib.vendor.spark;

import com.revrobotics.*;
import org.bitbuckets.lib.core.HasLogLoop;
import org.bitbuckets.lib.log.IDebuggable;
import org.bitbuckets.lib.hardware.IMotorController;
import org.bitbuckets.lib.hardware.MotorConfig;

public class SparkRelativeMotorController implements IMotorController, HasLogLoop {


    final MotorConfig motorConfig;
    final CANSparkMax sparkMax;
    final RelativeEncoder sparkMaxRelativeEncoder;
    final SparkMaxPIDController sparkMaxPIDController;
    final IDebuggable debuggable;

    final AbsoluteEncoder absoluteEncoder;
    final SparkMaxLimitSwitch forward;
    final SparkMaxLimitSwitch reverse;

    SparkRelativeMotorController(MotorConfig motorConfig, CANSparkMax sparkMax, IDebuggable debuggable) {
        this.motorConfig = motorConfig;
        this.sparkMax = sparkMax;
        this.sparkMaxPIDController = sparkMax.getPIDController();
        this.sparkMaxRelativeEncoder = sparkMax.getEncoder();
        if (motorConfig.hasAbsoluteEncoder) {
            this.absoluteEncoder = sparkMax.getAbsoluteEncoder(SparkMaxAbsoluteEncoder.Type.kDutyCycle);
        } else { this.absoluteEncoder = null; }

        this.debuggable = debuggable;
        if (motorConfig.isForwardHardLimitEnabled) {
            this.forward = sparkMax.getForwardLimitSwitch(SparkMaxLimitSwitch.Type.kNormallyOpen);
        } else { this.forward = null; }
        if (motorConfig.isBackwardHardLimitEnabled) {
            this.reverse = sparkMax.getReverseLimitSwitch(SparkMaxLimitSwitch.Type.kNormallyOpen);
        } else { this.reverse = null; }


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
        cachedPercent = percent;
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
        sparkMax.getBusVoltage();

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

        if (motorConfig.hasAbsoluteEncoder) {
            return absoluteEncoder.getPosition();
        } else {
            System.out.println("expected absolute encoder on soething configed without");
            return 0.0; //oh... oh no
        }
    }

    @Override
    public boolean isForwardLimitSwitchPressed() {
        if (motorConfig.isForwardHardLimitEnabled) {
            return forward.isPressed();
        } else {
            return false;
        }
    }

    @Override
    public boolean isReverseLimitSwitchPressed() {

        if (motorConfig.isBackwardHardLimitEnabled) {
            return reverse.isPressed();
        } else {
            return false;
        }

    }

    @Override
    public void goLimp() {

    }

    @Override
    public <T> T rawAccess(Class<T> clazz) throws UnsupportedOperationException {
        return clazz.cast(sparkMax);
    }

    @Override
    public void logLoop() {
        debuggable.log("last-voltage",  cachedVoltage);
        debuggable.log("last-percent", cachedPercent);
    }
}
