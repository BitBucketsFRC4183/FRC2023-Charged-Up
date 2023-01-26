package org.bitbuckets.lib.vendor.spark;

import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.SparkMaxPIDController;
import org.bitbuckets.lib.hardware.IMotorController;
import org.bitbuckets.lib.hardware.MotorIndex;
import org.bitbuckets.lib.log.DataLogger;
import org.bitbuckets.lib.vendor.ctre.TalonDataAutoGen;

public class SparkRelativeMotorController implements IMotorController, Runnable {


    final double[] motorConstants;
    final CANSparkMax sparkMax;
    final DataLogger<TalonDataAutoGen> dataLogger;

    final RelativeEncoder sparkMaxRelativeEncoder;
    final SparkMaxPIDController sparkMaxPIDController;

    SparkRelativeMotorController(double[] motorConstants, CANSparkMax sparkMax, DataLogger<TalonDataAutoGen> dataLogger) {
        this.motorConstants = motorConstants;
        this.sparkMax = sparkMax;
        this.dataLogger = dataLogger;
        this.sparkMaxPIDController = sparkMax.getPIDController();
        this.sparkMaxRelativeEncoder = sparkMax.getEncoder();
    }


    @Override
    public double getMechanismFactor() {
        return motorConstants[MotorIndex.MECHANISM_FACTOR];
    }

    @Override
    public double getRotationsToMetersFactor() {
        return motorConstants[MotorIndex.ROTATION_TO_METER_FACTOR];
    }

    @Override
    public double getRawToRotationsFactor() {
        return 1; //Sparks units are in rotations
    }

    @Override
    public double getTimeFactor() {
        return motorConstants[MotorIndex.TIME_FACTOR];
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
    public void moveAtPercent(double percent) {
        sparkMax.set(percent);
    }

    @Override
    public void moveToPosition(double position_encoderRotations) {
        sparkMaxPIDController.setReference(position_encoderRotations, CANSparkMax.ControlType.kPosition);
    }

    @Override
    public void moveAtVelocity(double velocity_encoderMetersPerSecond) {
        double rotationsPerMinute = velocity_encoderMetersPerSecond / getRotationsToMetersFactor() * 60.0;

        sparkMaxPIDController.setReference(rotationsPerMinute, CANSparkMax.ControlType.kVelocity);
    }

    @Override
    public double getSetpoint_rawUnits() {
        throw new UnsupportedOperationException("cannot use getSetpoint on a spark :(");
    }

    @Override
    public <T> T rawAccess(Class<T> clazz) throws UnsupportedOperationException {
        return clazz.cast(sparkMax);
    }

    @Override
    public void run() {
        dataLogger.process(data -> {

        });
    }
}
