package org.bitbuckets.drive.controlsds.neo;

import org.bitbuckets.drive.controlsds.sds.*;
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
        var logger = path.generatePushDataLogger(SwerveModuleDataAutoGen::new);
        return new SwerveModule(logger, driveController.build(path.addChild("drive-controller")),
                steerController.build(path.addChild("steer-controller")));
    }
}
