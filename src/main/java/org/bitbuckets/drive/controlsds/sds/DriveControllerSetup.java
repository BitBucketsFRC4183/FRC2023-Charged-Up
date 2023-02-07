package org.bitbuckets.drive.controlsds.sds;

import org.bitbuckets.drive.DriveConstants;
import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.hardware.IMotorController;
import org.bitbuckets.lib.ProcessPath;

public class DriveControllerSetup implements ISetup<IDriveController> {

    final ISetup<IMotorController> motor;

    public DriveControllerSetup(ISetup<IMotorController> motor) {
        this.motor = motor;
    }

    @Override
    public IDriveController build(ProcessPath path) {
        // pass our configured motorController back to the DriveController
        return new DriveController(this.motor.build(path.addChild("motor")), DriveConstants.nominalVoltage);
    }
}
