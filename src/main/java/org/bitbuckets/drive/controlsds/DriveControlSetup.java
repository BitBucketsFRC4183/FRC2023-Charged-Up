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
    public DriveControl build(ProcessPath self) {


        DriveControl control = new DriveControl(
                self.generateDebugger(),
                frontLeft.build(self.addChild("front-left")),
                frontRight.build(self.addChild("front-right")),
                backLeft.build(self.addChild("back-left")),
                backRight.build(self.addChild("back-right"))
        );
        
        self.registerLoop(control::log, "logging-loop");

        return control;
    }

}
