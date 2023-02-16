package org.bitbuckets.drive.controlsds;

import org.bitbuckets.drive.controlsds.sds.ISwerveModule;
import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.IProcess;

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
    public DriveControl build(IProcess self) {


        DriveControl control = new DriveControl(
                self.getDebuggable(),
                self.childSetup("front-left",frontLeft),
                self.childSetup("front-right",frontRight),
                self.childSetup("back-left",backLeft),
                self.childSetup("back-right",backRight)
        );
        
        self.registerLogLoop(control);

        return control;
    }

}
