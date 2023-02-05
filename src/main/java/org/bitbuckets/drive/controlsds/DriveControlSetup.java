package org.bitbuckets.drive.controlsds;

import org.bitbuckets.drive.controlsds.DriveControl;
import org.bitbuckets.drive.controlsds.sds.ISwerveModule;
import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.ProcessPath;

/**
 * Sets up prereqs for a drive controller
 * <p>
 */
public class DriveControlSetup implements ISetup<DriveControl> {

    final ISetup<ISwerveModule> frontLeft;
    final ISetup<ISwerveModule> frontRight;
    final ISetup<ISwerveModule> backLeft;
    final ISetup<ISwerveModule> backRight;

    public DriveControlSetup(ISetup<ISwerveModule> frontLeft, ISetup<ISwerveModule> frontRight, ISetup<ISwerveModule> backLeft, ISetup<ISwerveModule> backRight) {
        this.frontLeft = frontLeft;
        this.frontRight = frontRight;
        this.backLeft = backLeft;
        this.backRight = backRight;
    }

    @Override
    public DriveControl build(ProcessPath path) {


        DriveControl control = new DriveControl(
                frontLeft.build(path.addChild("front-left")),
                frontRight.build(path.addChild("front-right")),
                backLeft.build(path.addChild("back-left")),
                backRight.build(path.addChild("back-right")),
                path.generateStateLogger("desired-states"),
                path.generateStateLogger("actual-states")
        );

        path.registerLoop(control::guaranteedLoggingLoop, 30, "help me");

        return control;
    }

}
