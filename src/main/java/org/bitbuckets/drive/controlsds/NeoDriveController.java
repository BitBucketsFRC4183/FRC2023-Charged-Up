package org.bitbuckets.drive.controlsds;

import com.revrobotics.CANSparkMax;

public class NeoDriveController implements DriveController {


    private final CANSparkMax motor;

    private final double sensorVelocityCoefficient;
    private final double nominalVoltage;

    public NeoDriveController(CANSparkMax motor, double sensorVelocityCoefficient, double nominalVoltage) {
        this.motor = motor;
        this.sensorVelocityCoefficient = sensorVelocityCoefficient;
        this.nominalVoltage = nominalVoltage;
    }

    @Override
    public void setReferenceVoltage(double voltage) {
        motor.set(voltage / nominalVoltage);
    }

    @Override
    public double getStateVelocity() {
        return motor.getEncoder().getVelocity() * sensorVelocityCoefficient;
    }
}
