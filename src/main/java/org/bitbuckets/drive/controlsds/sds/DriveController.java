package org.bitbuckets.drive.controlsds.sds;

import org.bitbuckets.lib.hardware.IMotorController;

public class DriveController implements IDriveController {
    private final IMotorController motor;

    private final double nominalVoltage;

    public DriveController(IMotorController motor, double nominalVoltage) {
        this.motor = motor;
        this.nominalVoltage = nominalVoltage;
    }

    @Override
    public void setReferenceVoltage(double voltage) {
        motor.moveAtPercent(voltage / nominalVoltage);
    }

    @Override
    public double getStateVelocity() {
        return motor.getVelocityRaw();
    }
}
