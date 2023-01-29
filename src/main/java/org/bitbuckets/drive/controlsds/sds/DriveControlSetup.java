package org.bitbuckets.drive.controlsds.sds;

import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.wpilibj.interfaces.Gyro;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.bitbuckets.drive.DriveSDSConstants;
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

        double wheelWearFactor = 1;

        double maxVelocity_metersPerSecond = 60.0 *
                DriveSDSConstants.MK4I_L2.getDriveReduction() *
                (DriveSDSConstants.MK4I_L2.getWheelDiameter() * wheelWearFactor) *
                Math.PI;

        double maxAngularVelocity_radiansPerSecond =
                maxVelocity_metersPerSecond /
                        Math.hypot(DriveSDSConstants.drivetrainTrackWidth_meters / 2.0, DriveSDSConstants.drivetrainWheelBase_meters / 2.0);

        SmartDashboard.putNumber("/drivetrain/max_angular_velocity", maxAngularVelocity_radiansPerSecond);

        Translation2d moduleFrontLeftLocation =
                new Translation2d(DriveSDSConstants.drivetrainTrackWidth_meters / 2.0, DriveSDSConstants.drivetrainWheelBase_meters / 2.0);
        Translation2d moduleFrontRightLocation =
                new Translation2d(DriveSDSConstants.drivetrainTrackWidth_meters / 2.0, -DriveSDSConstants.drivetrainWheelBase_meters / 2.0);
        Translation2d moduleBackLeftLocation =
                new Translation2d(-DriveSDSConstants.drivetrainTrackWidth_meters / 2.0, DriveSDSConstants.drivetrainWheelBase_meters / 2.0);
        Translation2d moduleBackRightLocation =
                new Translation2d(
                        -DriveSDSConstants.drivetrainTrackWidth_meters / 2.0,
                        -DriveSDSConstants.drivetrainWheelBase_meters / 2.0
                );

        SwerveDriveKinematics kinematics =
                new SwerveDriveKinematics(
                        moduleFrontLeftLocation,
                        moduleFrontRightLocation,
                        moduleBackLeftLocation,
                        moduleBackRightLocation
                );

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
