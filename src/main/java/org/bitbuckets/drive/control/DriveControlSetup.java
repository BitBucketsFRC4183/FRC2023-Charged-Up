package org.bitbuckets.drive.control;

import org.bitbuckets.drive.module.DriveModule;
import org.bitbuckets.lib.ProcessPath;
import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.log.DataLogger;

/**
 * Sets up prereqs for a drive controller
 *
 * really fucking simple because a drivecontrol is super simple LMAO
 */
public class DriveControlSetup implements ISetup<DriveControl> {

    final ISetup<DriveModule> frontLeft;
    final ISetup<DriveModule> frontRight;
    final ISetup<DriveModule> backLeft;
    final ISetup<DriveModule> backRight;

    public DriveControlSetup(ISetup<DriveModule> frontLeft, ISetup<DriveModule> frontRight, ISetup<DriveModule> backLeft, ISetup<DriveModule> backRight) {
        this.frontLeft = frontLeft;
        this.frontRight = frontRight;
        this.backLeft = backLeft;
        this.backRight = backRight;
    }


    @Override
    public DriveControl build(ProcessPath path) {
        DataLogger<DriveControlDataAutoGen> logger = path.generatePushDataLogger(DriveControlDataAutoGen::new);
        DriveControl control = new DriveControl(
                logger,
                frontLeft.build(path.addChild("swerve-module-fl")),
                frontRight.build(path.addChild("swerve-module-fr")),
                backLeft.build(path.addChild("swerve-module-bl")),
                backRight.build(path.addChild("swerve-module-br"))
        );


        path.registerLoop(control::guaranteedLoggingLoop, "logging");

        return control;
    }
}
