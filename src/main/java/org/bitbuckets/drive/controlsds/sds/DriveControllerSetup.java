package org.bitbuckets.drive.controlsds.sds;

import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.hardware.IMotorController;
import org.bitbuckets.lib.IProcess;

public class DriveControllerSetup implements ISetup<IDriveController> {

    final ISetup<IMotorController> motor;

    public DriveControllerSetup(ISetup<IMotorController> motor) {
        this.motor = motor;
    }

    @Override
    public IDriveController build(IProcess self) {
        // pass our configured motorController back to the DriveController
        return new DriveController(self.inlineSetup(motor));
    }
}
