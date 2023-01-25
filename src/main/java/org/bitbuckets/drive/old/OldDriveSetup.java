package org.bitbuckets.drive.old;

import org.bitbuckets.drive.module.DriveModule;
import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.ProcessPath;
import org.bitbuckets.lib.log.DataLogger;

/**
 * Sets up prereqs for a drive controller
 * <p>
 * really simple because a drivecontrol is super simple LMAO
 */
public class OldDriveSetup implements ISetup<OldDriveControl> {

    final ISetup<DriveModule> frontLeft;
    final ISetup<DriveModule> frontRight;
    final ISetup<DriveModule> backLeft;
    final ISetup<DriveModule> backRight;

    public OldDriveSetup(ISetup<DriveModule> frontLeft, ISetup<DriveModule> frontRight, ISetup<DriveModule> backLeft, ISetup<DriveModule> backRight) {
        this.frontLeft = frontLeft;
        this.frontRight = frontRight;
        this.backLeft = backLeft;
        this.backRight = backRight;
    }


    @Override
    public OldDriveControl build(ProcessPath path) {
        DataLogger<OldDriveDataAutoGen> logger = path.generatePushDataLogger(OldDriveDataAutoGen::new);
        OldDriveControl control = new OldDriveControl(
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
