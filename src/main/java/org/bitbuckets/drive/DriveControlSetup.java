package org.bitbuckets.drive;

import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import org.bitbuckets.lib.IProcess;
import org.bitbuckets.lib.ISetup;

/**
 * Sets up prereqs for a drive controller
 * <p>
 */
public class DriveControlSetup implements ISetup<IDriveControl> {

    final SwerveDriveKinematics kinematics;
    final ISetup<ISwerveModule> frontLeft;
    final ISetup<ISwerveModule> frontRight;
    final ISetup<ISwerveModule> backLeft;
    final ISetup<ISwerveModule> backRight;

    public DriveControlSetup(SwerveDriveKinematics kinematics, ISetup<ISwerveModule> frontLeft, ISetup<ISwerveModule> frontRight, ISetup<ISwerveModule> backLeft, ISetup<ISwerveModule> backRight) {
        this.kinematics = kinematics;
        this.frontLeft = frontLeft;
        this.frontRight = frontRight;
        this.backLeft = backLeft;
        this.backRight = backRight;
    }

    @Override
    public DriveControl build(IProcess self) {


        DriveControl control = new DriveControl(
                kinematics,
                self.getDebuggable(),
                self.childSetup("frontLeft",frontLeft),
                self.childSetup("frontRight",frontRight),
                self.childSetup("backLeft",backLeft),
                self.childSetup("backRight",backRight)
        );
        
        self.registerLogLoop(control);

        return control;
    }

}
