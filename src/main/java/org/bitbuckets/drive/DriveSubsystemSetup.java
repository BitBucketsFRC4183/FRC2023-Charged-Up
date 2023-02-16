package org.bitbuckets.drive;

import edu.wpi.first.wpilibj.Joystick;
import org.bitbuckets.auto.AutoSubsystem;
import org.bitbuckets.drive.balance.ClosedLoopsControl;
import org.bitbuckets.drive.balance.ClosedLoopsSetup;
import org.bitbuckets.drive.controlsds.DriveControl;
import org.bitbuckets.drive.controlsds.DriveControlSetup;
import org.bitbuckets.drive.controlsds.sds.DriveControllerSetup;
import org.bitbuckets.drive.controlsds.sds.SteerControllerSetup;
import org.bitbuckets.drive.controlsds.sds.SwerveModuleSetup;
import org.bitbuckets.drive.holo.HoloControl;
import org.bitbuckets.drive.holo.HoloControlSetup;
import org.bitbuckets.lib.IProcess;
import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.log.IDebuggable;
import org.bitbuckets.lib.tune.IValueTuner;
import org.bitbuckets.lib.util.MockingUtil;
import org.bitbuckets.lib.vendor.ctre.CANCoderAbsoluteEncoderSetup;
import org.bitbuckets.lib.vendor.ctre.TalonDriveMotorSetup;
import org.bitbuckets.lib.vendor.ctre.TalonSteerMotorSetup;
import org.bitbuckets.lib.vendor.sim.dc.DCSimSetup;
import org.bitbuckets.lib.vendor.spark.SparkDriveMotorSetup;
import org.bitbuckets.lib.vendor.spark.SparkSteerMotorSetup;
import org.bitbuckets.lib.vendor.thrifty.ThriftyEncoderSetup;
import org.bitbuckets.odometry.IOdometryControl;
import org.bitbuckets.odometry.OdometryControlSetup;
import org.bitbuckets.vision.IVisionControl;

public class DriveSubsystemSetup implements ISetup<DriveSubsystem> {

    final boolean driveEnabled;
    final boolean isSimulated;

    final AutoSubsystem autoSubsystem;
    final IVisionControl visionControl;

    public DriveSubsystemSetup(boolean driveEnabled, boolean isSimulated, AutoSubsystem autoSubsystem, IVisionControl visionControl) {
        this.driveEnabled = driveEnabled;
        this.isSimulated = isSimulated;
        this.autoSubsystem = autoSubsystem;
        this.visionControl = visionControl;
    }

    @Override
    public DriveSubsystem build(IProcess self) {
        if (!driveEnabled) {
            return MockingUtil.buddy(DriveSubsystem.class);
        }

        DriveInput input = new DriveInput(new Joystick(0));
        ClosedLoopsControl closedLoopsControl = self.childSetup("axis-control", new ClosedLoopsSetup());

        DriveControl driveControl;
        if (isSimulated) {
            driveControl = buildSimDriveControl(self);
        } else {
            driveControl = buildNeoDriveControl(self); //or use talons, when they work}
        }

        autoSubsystem.setDriveControl(driveControl);
        IOdometryControl odometryControl = self.childSetup("odo-control", new OdometryControlSetup(driveControl, visionControl, 5));
        autoSubsystem.setOdometryControl(odometryControl);

        HoloControl holoControl = self.childSetup("holo-control", new HoloControlSetup(driveControl, visionControl, odometryControl));

        IValueTuner<DriveSubsystem.OrientationChooser> orientationTuner = self
                .generateTuner(DriveSubsystem.OrientationChooser.class, "set-orientation", DriveSubsystem.OrientationChooser.FIELD_ORIENTED);
        IDebuggable debuggable = self.getDebuggable();

        return new DriveSubsystem(
                input,
                odometryControl,
                closedLoopsControl,
                driveControl,
                autoSubsystem,
                holoControl,
                visionControl,
                orientationTuner,
                debuggable

        );

    }


    DriveControl buildNeoDriveControl(IProcess path) {
        // used to configure the spark motor in SparkSetup
        DriveControlSetup driveControl = new DriveControlSetup(
                new SwerveModuleSetup(
                        new DriveControllerSetup(new SparkDriveMotorSetup(DriveConstants.FRONT_LEFT_DRIVE_ID, DriveConstants.DRIVE_CONFIG, DriveConstants.MK4I_L2)),
                        new SteerControllerSetup(
                                new SparkSteerMotorSetup(DriveConstants.FRONT_LEFT_STEER_ID, DriveConstants.STEER_CONFIG, DriveConstants.STEER_PID, DriveConstants.MK4I_L2),
                                new ThriftyEncoderSetup(DriveConstants.FRONT_LEFT_ENCODER_CHANNEL, DriveConstants.FRONT_LEFT_OFFSET))
                ),
                new SwerveModuleSetup(
                        new DriveControllerSetup(new SparkDriveMotorSetup(DriveConstants.FRONT_RIGHT_DRIVE_ID, DriveConstants.DRIVE_CONFIG, DriveConstants.MK4I_L2)),
                        new SteerControllerSetup(
                                new SparkSteerMotorSetup(DriveConstants.FRONT_RIGHT_STEER_ID, DriveConstants.STEER_CONFIG, DriveConstants.STEER_PID, DriveConstants.MK4I_L2),
                                new ThriftyEncoderSetup(DriveConstants.FRONT_RIGHT_ENCODER_CHANNEL, DriveConstants.FRONT_RIGHT_OFFSET))
                ),
                new SwerveModuleSetup(
                        new DriveControllerSetup(new SparkDriveMotorSetup(DriveConstants.BACK_LEFT_DRIVE_ID, DriveConstants.DRIVE_CONFIG, DriveConstants.MK4I_L2)),
                        new SteerControllerSetup(
                                new SparkSteerMotorSetup(DriveConstants.BACK_LEFT_STEER_ID, DriveConstants.STEER_CONFIG, DriveConstants.STEER_PID, DriveConstants.MK4I_L2),
                                new ThriftyEncoderSetup(DriveConstants.BACK_LEFT_ENCODER_CHANNEL, DriveConstants.BACK_LEFT_OFFSET))
                ),
                new SwerveModuleSetup(
                        new DriveControllerSetup(new SparkDriveMotorSetup(DriveConstants.BACK_RIGHT_DRIVE_ID, DriveConstants.DRIVE_CONFIG, DriveConstants.MK4I_L2)),
                        new SteerControllerSetup(
                                new SparkSteerMotorSetup(DriveConstants.BACK_RIGHT_STEER_ID, DriveConstants.STEER_CONFIG, DriveConstants.STEER_PID, DriveConstants.MK4I_L2),
                                new ThriftyEncoderSetup(DriveConstants.BACK_RIGHT_ENCODER_CHANNEL, DriveConstants.BACK_RIGHT_OFFSET))
                )
        );

        return path.childSetup("neo-drive", driveControl);
    }

    DriveControl buildSimDriveControl(IProcess path) {
        DriveControlSetup driveControl = new DriveControlSetup(
                new SwerveModuleSetup(
                        new DriveControllerSetup(new DCSimSetup(DriveConstants.DRIVE_CONFIG, DriveConstants.DRIVE_MOTOR, DriveConstants.DRIVE_PID)),
                        new SteerControllerSetup(
                                new DCSimSetup(DriveConstants.STEER_CONFIG, DriveConstants.STEER_MOTOR, DriveConstants.STEER_PID),
                                new ThriftyEncoderSetup(DriveConstants.FRONT_LEFT_ENCODER_CHANNEL, DriveConstants.FRONT_LEFT_OFFSET))
                ),
                new SwerveModuleSetup(
                        new DriveControllerSetup(new DCSimSetup(DriveConstants.DRIVE_CONFIG, DriveConstants.DRIVE_MOTOR, DriveConstants.DRIVE_PID)),
                        new SteerControllerSetup(
                                new DCSimSetup(DriveConstants.STEER_CONFIG, DriveConstants.STEER_MOTOR, DriveConstants.STEER_PID),
                                new ThriftyEncoderSetup(DriveConstants.FRONT_RIGHT_ENCODER_CHANNEL, DriveConstants.FRONT_RIGHT_OFFSET))
                ),
                new SwerveModuleSetup(
                        new DriveControllerSetup(new DCSimSetup(DriveConstants.DRIVE_CONFIG, DriveConstants.DRIVE_MOTOR, DriveConstants.DRIVE_PID)),
                        new SteerControllerSetup(
                                new DCSimSetup(DriveConstants.STEER_CONFIG, DriveConstants.STEER_MOTOR, DriveConstants.STEER_PID),
                                new ThriftyEncoderSetup(DriveConstants.BACK_LEFT_ENCODER_CHANNEL, DriveConstants.BACK_LEFT_OFFSET))
                ),
                new SwerveModuleSetup(
                        new DriveControllerSetup(new DCSimSetup(DriveConstants.DRIVE_CONFIG, DriveConstants.DRIVE_MOTOR, DriveConstants.DRIVE_PID)),
                        new SteerControllerSetup(
                                new DCSimSetup(DriveConstants.STEER_CONFIG, DriveConstants.STEER_MOTOR, DriveConstants.STEER_PID),
                                new ThriftyEncoderSetup(DriveConstants.BACK_RIGHT_ENCODER_CHANNEL, DriveConstants.BACK_RIGHT_OFFSET))
                )
        );

        return path.childSetup("sim-drive", driveControl);
    }


    DriveControl buildTalonDriveControl(IProcess path) {

        int frontLeftModuleDriveMotor_ID = 1;
        int frontLeftModuleSteerMotor_ID = 2;
        int frontLeftModuleSteerEncoder_ID = 9;

        int frontRightModuleDriveMotor_ID = 7;
        int frontRightModuleSteerMotor_ID = 8;
        int frontRightModuleSteerEncoder_ID = 12;

        int backLeftModuleDriveMotor_ID = 5;
        int backLeftModuleSteerMotor_ID = 6;
        int backLeftModuleSteerEncoder_ID = 11;

        int backRightModuleDriveMotor_ID = 3;
        int backRightModuleSteerMotor_ID = 4;
        int backRightModuleSteerEncoder_ID = 10;

        double frontLeftModuleSteerOffset = -Math.toRadians(51.6); // set front left steer offset

        double frontRightModuleSteerOffset = -Math.toRadians(146.7 - 180); // set front right steer offset

        double backLeftModuleSteerOffset = -Math.toRadians(254.9 + 180); // set back left steer offset

        double backRightModuleSteerOffset = -Math.toRadians(66.7); // set back right steer offset

        double sensorPositionCoefficient = 2.0 * Math.PI / 2048 * DriveConstants.MK4_L2.getSteerReduction();

        DriveControlSetup setup =  new DriveControlSetup(
                new SwerveModuleSetup(
                        new DriveControllerSetup(new TalonDriveMotorSetup(frontLeftModuleDriveMotor_ID, DriveConstants.MK4_L2)),
                        new SteerControllerSetup(
                                new TalonSteerMotorSetup(frontLeftModuleSteerMotor_ID, DriveConstants.MK4_L2),
                                new CANCoderAbsoluteEncoderSetup(frontLeftModuleSteerEncoder_ID, frontLeftModuleSteerOffset),
                                sensorPositionCoefficient
                        )
                ),
                new SwerveModuleSetup(
                        new DriveControllerSetup(new TalonDriveMotorSetup(frontRightModuleDriveMotor_ID, DriveConstants.MK4_L2)),
                        new SteerControllerSetup(
                                new TalonSteerMotorSetup(frontRightModuleSteerMotor_ID, DriveConstants.MK4_L2),
                                new CANCoderAbsoluteEncoderSetup(frontRightModuleSteerEncoder_ID, frontRightModuleSteerOffset),
                                sensorPositionCoefficient
                        )
                ),
                new SwerveModuleSetup(
                        new DriveControllerSetup(new TalonDriveMotorSetup(backLeftModuleDriveMotor_ID, DriveConstants.MK4_L2)),
                        new SteerControllerSetup(
                                new TalonSteerMotorSetup(backLeftModuleSteerMotor_ID, DriveConstants.MK4_L2),
                                new CANCoderAbsoluteEncoderSetup(backLeftModuleSteerEncoder_ID, backLeftModuleSteerOffset),
                                sensorPositionCoefficient
                        )
                ),
                new SwerveModuleSetup(
                        new DriveControllerSetup(new TalonDriveMotorSetup(backRightModuleDriveMotor_ID, DriveConstants.MK4_L2)),
                        new SteerControllerSetup(
                                new TalonSteerMotorSetup(backRightModuleSteerMotor_ID, DriveConstants.MK4_L2),
                                new CANCoderAbsoluteEncoderSetup(backRightModuleSteerEncoder_ID, backRightModuleSteerOffset),
                                sensorPositionCoefficient
                        )
                )
        );

        return path.childSetup("talon-drive", setup);
    }

}
