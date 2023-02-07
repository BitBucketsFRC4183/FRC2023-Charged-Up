package org.bitbuckets.drive.controlsds.sds;

import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.log.ILoggable;
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
        ILoggable<double[]> swerveAngleVoltage = path.generateDoubleLoggers("command-degrees", "command-po");
        SwerveModule swerveModule = new SwerveModule(
                driveController.build(path.addChild("drive-controller")),
                steerController.build(path.addChild("steer-controller")),
                swerveAngleVoltage
        );

        path.registerLoop(swerveModule, 20, "swerve-log-loop");

        return swerveModule;
    }
}
