package org.bitbuckets.drive.controlsds.sds;

import edu.wpi.first.wpilibj.interfaces.Gyro;
import org.bitbuckets.drive.controlsds.DriveControl;
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

    final Gyro gyro;

    public DriveControlSetup(ISetup<ISwerveModule> frontLeft, ISetup<ISwerveModule> frontRight, ISetup<ISwerveModule> backLeft, ISetup<ISwerveModule> backRight, Gyro gyro) {
        this.frontLeft = frontLeft;
        this.frontRight = frontRight;
        this.backLeft = backLeft;
        this.backRight = backRight;
        this.gyro = gyro;
    }

    @Override
    public DriveControl build(ProcessPath path) {

        //Calibrate the gyro only once when the drive subsystem is first initialized
        gyro.calibrate();

        DriveControl control = new DriveControl(
                frontLeft.build(path.addChild("front-left")),
                frontRight.build(path.addChild("front-right")),
                backLeft.build(path.addChild("back-left")),
                backRight.build(path.addChild("back-right")),
                gyro,
                path.generateStateLogger("desired-states"),
                path.generateStateLogger("actual-states")
        );

        path.registerLoop(control::guaranteedLoggingLoop, "logging");

        return control;
    }

}
