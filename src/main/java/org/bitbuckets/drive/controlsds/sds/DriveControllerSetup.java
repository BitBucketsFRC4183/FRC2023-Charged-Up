package org.bitbuckets.drive.controlsds.sds;

import org.bitbuckets.drive.DriveConstants;
import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.ProcessPath;
import org.bitbuckets.lib.hardware.IMotorController;

public class DriveControllerSetup implements ISetup<IDriveController> {

    final ISetup<IMotorController> motor;

    public DriveControllerSetup(ISetup<IMotorController> motor) {
        this.motor = motor;
    }

    @Override
    public IDriveController build(ProcessPath self) {
        // pass our configured motorController back to the DriveController
        return new DriveController(this.motor.build(self.addChild("motor")), DriveConstants.nominalVoltage);
    }
}
