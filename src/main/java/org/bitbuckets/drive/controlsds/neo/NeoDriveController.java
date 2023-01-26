package org.bitbuckets.drive.controlsds.neo;

import com.revrobotics.CANSparkMax;
import org.bitbuckets.drive.controlsds.sds.DriveController;

public class NeoDriveController implements DriveController {


    private final CANSparkMax motor;

    private final double nominalVoltage;

    public NeoDriveController(CANSparkMax motor, double nominalVoltage) {
        this.motor = motor;
        this.nominalVoltage = nominalVoltage;
    }

    @Override
    public void setReferenceVoltage(double voltage) {
        motor.set(voltage / nominalVoltage);
    }

    @Override
    public double getStateVelocity() {
        return motor.getEncoder().getVelocity();
    }
}
