package org.bitbuckets.drive.controlsds.sds;

import org.bitbuckets.lib.hardware.IMotorController;

public class DriveController implements IDriveController {
    private final IMotorController motor;

    public DriveController(IMotorController motor) {
        this.motor = motor;
    }

    @Override
    public void setReferenceVoltage(double voltage) {
        motor.moveAtVoltage(voltage);
    }

    @Override
    public double getStateVelocity() {
        return motor.getVelocityMechanism_metersPerSecond();
    }

    @Override
    public double getStatePosition_meters() {
        return -motor.getPositionMechanism_meters();
    }


}
