package org.bitbuckets.drive.controlsds;

import com.ctre.phoenix.sensors.WPI_PigeonIMU;
import com.swervedrivespecialties.swervelib.Mk4SwerveModuleHelper;
import com.swervedrivespecialties.swervelib.SdsModuleConfigurations;
import com.swervedrivespecialties.swervelib.SwerveModule;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInLayouts;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.bitbuckets.drive.DriveSDSConstants;
import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.ProcessPath;
import org.bitbuckets.lib.log.DataLogger;

/**
 * Sets up prereqs for a drive controller
 * <p>
 * really fucking simple because a drivecontrol is super simple LMAO
 */
public class DriveControlSDSSetup implements ISetup<DriveControlSDS> {


    @Override
    public DriveControlSDS build(ProcessPath path) {
        DataLogger<DriveControlSDSDataAutoGen> logger = path.generatePushDataLogger(DriveControlSDSDataAutoGen::new);

        double wheelWearFactor = 1;

        double maxVelocity_metersPerSecond =
                6380.0 /
                        60.0 *
                        SdsModuleConfigurations.MK4_L2.getDriveReduction() *
                        (SdsModuleConfigurations.MK4_L2.getWheelDiameter() * wheelWearFactor) *
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

        WPI_PigeonIMU gyro = new WPI_PigeonIMU(5);


        ShuffleboardTab tab = Shuffleboard.getTab("Drivetrain");

        // There are 4 methods you can call to create your swerve modules.
        // The method you use depends on what motors you are using.
        //
        // Mk3SwerveModuleHelper.createFalcon500(...)
        // Your module has two Falcon 500s on it. One for steering and one for driving.
        //
        // Similar helpers also exist for Mk4 modules using the Mk4SwerveModuleHelper class.

        // By default we will use Falcon 500s in standard configuration. But if you use
        // a different configuration or motors
        // you MUST change it. If you do not, your code will crash on startup.
        // Setup motor configuration
        SwerveModule moduleFrontLeft = Mk4SwerveModuleHelper.createFalcon500(
                //Smart Dashboard Tab
                tab.getLayout("Front Left Module", BuiltInLayouts.kList).withSize(2, 4).withPosition(0, 0),
                Mk4SwerveModuleHelper.GearRatio.L2, //Gear Ratio
                DriveSDSConstants.frontLeftModuleDriveMotor_ID, //Drive Motor
                DriveSDSConstants.frontLeftModuleSteerMotor_ID, //Steer Motor
                DriveSDSConstants.frontLeftModuleSteerEncoder_ID, //Steer Encoder
                DriveSDSConstants.frontLeftModuleSteerOffset //Steer Offset
        );

        SwerveModule moduleFrontRight = Mk4SwerveModuleHelper.createFalcon500(
                //Smart Dashboard Tab
                tab.getLayout("Front Right Module", BuiltInLayouts.kList).withSize(2, 4).withPosition(2, 0),
                Mk4SwerveModuleHelper.GearRatio.L2, //Gear Ratio
                DriveSDSConstants.frontRightModuleDriveMotor_ID, //Drive Motor
                DriveSDSConstants.frontRightModuleSteerMotor_ID, //Steer Motor
                DriveSDSConstants.frontRightModuleSteerEncoder_ID, //Steer Encoder
                DriveSDSConstants.frontRightModuleSteerOffset //Steer Offset
        );

        SwerveModule moduleBackLeft = Mk4SwerveModuleHelper.createFalcon500(
                //Smart Dashboard Tab
                tab.getLayout("Back Left Module", BuiltInLayouts.kList).withSize(2, 4).withPosition(4, 0),
                Mk4SwerveModuleHelper.GearRatio.L2, //Gear Ratio
                DriveSDSConstants.backLeftModuleDriveMotor_ID, //Drive Motor
                DriveSDSConstants.backLeftModuleSteerMotor_ID, //Steer Motor
                DriveSDSConstants.backLeftModuleSteerEncoder_ID, //Steer Encoder
                DriveSDSConstants.backLeftModuleSteerOffset //Steer Offset
        );

        SwerveModule moduleBackRight = Mk4SwerveModuleHelper.createFalcon500(
                //Smart Dashboard Tab
                tab.getLayout("Back Right Module", BuiltInLayouts.kList).withSize(2, 4).withPosition(6, 0),
                Mk4SwerveModuleHelper.GearRatio.L2, //Gear Ratio
                DriveSDSConstants.backRightModuleDriveMotor_ID, //Drive Motor
                DriveSDSConstants.backRightModuleSteerMotor_ID, //Steer Motor
                DriveSDSConstants.backRightModuleSteerEncoder_ID, //Steer Encoder
                DriveSDSConstants.backRightModuleSteerOffset //Steer Offset
        );


        //Calibrate the gyro only once when the drive subsystem is first initialized
        gyro.calibrate();

        DriveControlSDS control = new DriveControlSDS(logger, maxVelocity_metersPerSecond, maxAngularVelocity_radiansPerSecond,
                gyro, moduleFrontLeft, moduleFrontRight, moduleBackLeft, moduleBackRight, kinematics);


        path.registerLoop(control::guaranteedLoggingLoop, "logging");

        return control;
    }

}
