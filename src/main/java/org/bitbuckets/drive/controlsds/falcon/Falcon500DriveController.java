package org.bitbuckets.drive.controlsds.falcon;

/*

@Deprecated
public class Falcon500DriveController implements DriveController {
    private final WPI_TalonFX motor;
    private final double sensorVelocityCoefficient;
    private final double nominalVoltage;

    public Falcon500DriveController(WPI_TalonFX motor, double sensorVelocityCoefficient, double nominalVoltage) {
        this.motor = motor;
        this.sensorVelocityCoefficient = sensorVelocityCoefficient;
        this.nominalVoltage = nominalVoltage;
    }

    @Override
    public void setReferenceVoltage(double voltage) {
        motor.set(TalonFXControlMode.PercentOutput, voltage / nominalVoltage);
    }

    @Override
    public double getStateVelocity() {
        return motor.getSelectedSensorVelocity() * sensorVelocityCoefficient;
    }
}*/