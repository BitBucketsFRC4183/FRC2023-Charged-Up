package org.bitbuckets.drive.controlsds.sds;

import org.bitbuckets.lib.ILogAs;
import org.bitbuckets.lib.IProcess;
import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.log.ILoggable;
import org.bitbuckets.lib.util.MockingUtil;

public class SwerveModuleSetup implements ISetup<ISwerveModule> {
    final ISetup<IDriveController> driveController;
    final ISetup<ISteerController> steerController;

    public SwerveModuleSetup(ISetup<IDriveController> driveController, ISetup<ISteerController> steerController) {
        this.driveController = driveController;
        this.steerController = steerController;
    }

    @Override
    public ISwerveModule build(IProcess self) {
        ILoggable<Double> percentOutput = self.generateLogger(ILogAs.DOUBLE, "percent-command");
        ILoggable<Double> angleCommand = self.generateLogger(ILogAs.DOUBLE, "angle-command");

        return new SwerveModule(
                self.childSetup("drive-controller", driveController),
                self.childSetup("steer-controller", steerController),
                angleCommand,
                percentOutput
        );
    }
}
