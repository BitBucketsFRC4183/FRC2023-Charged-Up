package org.bitbuckets.lib.vendor.spark;

import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.SparkMaxPIDController;
import org.bitbuckets.lib.hardware.IMotorController;
import org.bitbuckets.lib.hardware.MotorConfig;
import org.bitbuckets.lib.log.ILoggable;

public class SparkRelativeMotorController implements IMotorController, Runnable {


    final MotorConfig motorConfig;
    final CANSparkMax sparkMax;
    final RelativeEncoder sparkMaxRelativeEncoder;
    final SparkMaxPIDController sparkMaxPIDController;
    final ILoggable<double[]> motorData;
    SparkRelativeMotorController(MotorConfig motorConfig, CANSparkMax sparkMax, ILoggable<double[]> motorData) {
        this.motorConfig = motorConfig;
        this.sparkMax = sparkMax;
        this.sparkMaxPIDController = sparkMax.getPIDController();
        this.sparkMaxRelativeEncoder = sparkMax.getEncoder();
        this.motorData = motorData;
    }


    @Override
    public double getMechanismFactor() {
        return motorConfig.mechanismCoefficient;
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
    public void moveAtVoltage(double voltage) {
        sparkMax.setVoltage(voltage);
    }

    @Override
    public void moveAtPercent(double percent) {
        sparkMax.set(percent);
    }

    double positionSetpoint;

    @Override
    public void moveToPosition(double position_encoderRotations) {
        sparkMaxPIDController.setReference(position_encoderRotations, CANSparkMax.ControlType.kPosition);

        positionSetpoint = position_encoderRotations;
    }

    @Override
    public void moveToPosition_mechanismRotations(double position_mechanismRotations) {
        throw new UnsupportedOperationException(); //TODO fill thsi out
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

        double appliedOutput = sparkMax.getAppliedOutput();
        double busVoltage = sparkMax.getBusVoltage();

        double positionRotations = sparkMaxRelativeEncoder.getPosition();
        double velocityRotations = sparkMaxRelativeEncoder.getVelocity();


        double setpoint = positionSetpoint;
        double error = setpoint - getPositionRaw();


        //labels: high priority
        //TODO figure out how to get error from a sparkmax

        motorData.log(new double[]{
                appliedOutput,
                busVoltage,
                positionRotations,
                velocityRotations,
                setpoint,
                error
        });


    }
}
