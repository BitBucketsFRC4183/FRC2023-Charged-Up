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
        // This is inverted because the motor is inverted, which means positive voltage makes the wheels spin
        // backwards. When we go "forward" the encoder is actually going backwards. Invert here so our position updates
        // correctly

        //TODO remove the craig hack (commit specific and probably bad idea)
        return motor.getPositionMechanism_meters();
    }


}
