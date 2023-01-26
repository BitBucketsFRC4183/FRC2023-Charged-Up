package org.bitbuckets.lib.vendor.ctre;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.TalonFXControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import org.bitbuckets.lib.hardware.IMotorController;
import org.bitbuckets.lib.log.DataLogger;

/**
 * Talon, it's an encoder (hall) and motor all in one!
 * I hate robots, return to manual impulse control
 */
class TalonMotorController implements IMotorController, Runnable {

    @Override
    public void run() {
        dataLogger.process(data -> {
            data.isResetting = talonFX.hasResetOccurred();
            data.setpoint_encoderSU = getSetpoint_rawUnits();
            data.read_SU = getPositionRaw();
            data.read_encoderAccumRads = getEncoderPositionAccumulated_radians();
            data.read_encoderBoundRads = getEncoderPositionBounded_radians();
            data.read_mechAccumRads = getMechanismPositionAccumulated_radians();
            data.read_mechBoundRads = getMechanismPositionBounded_radians();
        });
    }

    final TalonFX talonFX;
    final double mechanismFactor;
    final double rotationsToMetersFactor;
    final DataLogger<TalonDataAutoGen> dataLogger;


    TalonMotorController(TalonFX talonFX, double mechanismFactor, double rotationsToMetersFactor, DataLogger<TalonDataAutoGen> dataLogger) {
        this.talonFX = talonFX;
        this.mechanismFactor = mechanismFactor;
        this.rotationsToMetersFactor = rotationsToMetersFactor;
        this.dataLogger = dataLogger;
    }

    static final double SU_TO_ROTATIONS = (1.0 / 2048.0);
    static final double MS_TO_S = 10.0; //convert from seconds-denominator to talon standard 100ms/denominator

    @Override
    public double getMechanismFactor() {
        return mechanismFactor;
    }

    @Override
    public double getRotationsToMetersFactor() {
        return rotationsToMetersFactor;
    }

    @Override
    public double getRawToRotationsFactor() {
        return SU_TO_ROTATIONS;
    }

    @Override
    public double getTimeFactor() {
        return MS_TO_S;
    }

    @Override
    public double getPositionRaw() {
        return talonFX.getSelectedSensorPosition();
    }

    @Override
    public double getVelocityRaw() {
        return talonFX.getSelectedSensorVelocity();
    }

    @Override
    public void forceOffset(double offsetUnits_baseUnits) {
        talonFX.setSelectedSensorPosition(offsetUnits_baseUnits);
    }

    @Override
    public void moveAtPercent(double percent) {
        talonFX.set(ControlMode.PercentOutput, percent);
    }

    @Override
    public void moveToPosition(double position_encoderRotations) {
        double position_sensorUnits = position_encoderRotations / SU_TO_ROTATIONS; //ROTATIONS_TO_SU

        //TODO signal being used in psoition mode ,

        talonFX.set(ControlMode.Position, position_sensorUnits);
    }

    @Override
    public void moveAtVelocity(double velocity_encoderMetersPerSecond) {         //ROTATIONS_TO_SU / S TO MS
        double velocity_encoderSensorUnitsPerMs = velocity_encoderMetersPerSecond / SU_TO_ROTATIONS / MS_TO_S;

        talonFX.set(TalonFXControlMode.Velocity, velocity_encoderSensorUnitsPerMs);
    }

    @Override
    public double getSetpoint_rawUnits() {
        //TODO make it work with percent output
        return talonFX.getClosedLoopTarget(0);
    }

    @Override
    public <T> T rawAccess(Class<T> clazz) throws UnsupportedOperationException {
        if (clazz != TalonFX.class) throw new UnsupportedOperationException();

        return clazz.cast(TalonFX.class);
    }
}
