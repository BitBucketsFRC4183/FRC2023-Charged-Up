package org.bitbuckets.drive.controlsds.sds;

import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.ProcessPath;
import org.bitbuckets.lib.log.ILoggable;

public class SwerveModuleSetup implements ISetup<ISwerveModule> {
    final ISetup<IDriveController> driveController;
    final ISetup<ISteerController> steerController;

    public SwerveModuleSetup(ISetup<IDriveController> driveController, ISetup<ISteerController> steerController) {
        this.driveController = driveController;
        this.steerController = steerController;
    }

    @Override
    public ISwerveModule build(ProcessPath self) {
        ILoggable<double[]> swerveAngleVoltage = self.generateDoubleLoggers("command-degrees", "command-po");
        SwerveModule swerveModule = new SwerveModule(
                driveController.build(self.addChild("drive-controller")),
                steerController.build(self.addChild("steer-controller")),
                swerveAngleVoltage
        );

        self.registerLoop(swerveModule, 20, "swerve-log-loop");

        return swerveModule;
    }
}
