package org.bitbuckets.drive.controlsds.neo;

import org.bitbuckets.drive.controlsds.sds.IDriveController;
import org.bitbuckets.drive.controlsds.sds.ISteerController;
import org.bitbuckets.drive.controlsds.sds.ISwerveModule;
import org.bitbuckets.drive.controlsds.sds.SwerveModule;
import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.ProcessPath;

public class SwerveModuleSetup implements ISetup<ISwerveModule> {
    final ISetup<IDriveController> driveController;
    final ISetup<ISteerController> steerController;

    public SwerveModuleSetup(ISetup<IDriveController> driveController, ISetup<ISteerController> steerController) {
        this.driveController = driveController;
        this.steerController = steerController;
    }

    @Override
    public ISwerveModule build(ProcessPath path) {
        return new SwerveModule(driveController.build(path.addChild("drive-controller")),
                steerController.build(path.addChild("steer-controller")));
    }
}
