package org.bitbuckets.drive.controlsds.neo;

import org.bitbuckets.drive.controlsds.sds.IDriveController;
import org.bitbuckets.lib.hardware.IMotorController;

public class NeoDriveController implements IDriveController {


    private final IMotorController motor;

    private final double nominalVoltage;

    public NeoDriveController(IMotorController motor, double nominalVoltage) {
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
