package org.bitbuckets.drive;

import config.Drive;
import config.DriveTurdSpecific;
import org.bitbuckets.OperatorInput;
import org.bitbuckets.auto.AutoSubsystem;
import org.bitbuckets.drive.balance.BalanceControl;
import org.bitbuckets.drive.balance.BalanceSetup;
import org.bitbuckets.drive.controlsds.DriveControl;
import org.bitbuckets.drive.controlsds.DriveControlSetup;
import org.bitbuckets.drive.controlsds.sds.DriveControllerSetup;
import org.bitbuckets.drive.controlsds.sds.ISwerveModule;
import org.bitbuckets.drive.controlsds.sds.SteerControllerSetup;
import org.bitbuckets.drive.controlsds.sds.SwerveModuleSetup;
import org.bitbuckets.drive.holo.HoloControl;
import org.bitbuckets.drive.holo.HoloControlSetup;
import org.bitbuckets.lib.IProcess;
import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.ITuneAs;
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
import org.bitbuckets.odometry.NavXOdometryControlSetup;
import org.bitbuckets.odometry.PidgeonOdometryControlSetup;
import org.bitbuckets.vision.IVisionControl;

public class DriveSubsystemSetup implements ISetup<DriveSubsystem> {

    public enum Mode {
        Neo,
        Talon,
        Sim
    }

    final boolean driveEnabled;
    final Mode mode;

    final OperatorInput operatorInput;
    final AutoSubsystem autoSubsystem;
    final IVisionControl visionControl;



    public DriveSubsystemSetup(boolean driveEnabled, Mode mode, OperatorInput input, AutoSubsystem autoSubsystem, IVisionControl visionControl) {
        this.driveEnabled = driveEnabled;
        this.mode = mode;
        this.autoSubsystem = autoSubsystem;
        this.visionControl = visionControl;
        this.operatorInput = input;
    }

    @Override
    public DriveSubsystem build(IProcess self) {
        if (!driveEnabled) {
            return MockingUtil.buddy(DriveSubsystem.class);
        }

        BalanceControl balanceControl = self.childSetup("closed-loop", new BalanceSetup());

        DriveControl driveControl;
        IOdometryControl odometryControl;

        switch (mode) {
            case Neo:
                driveControl = buildNeoDriveControl(self);
                odometryControl = self.childSetup("odo-control", new PidgeonOdometryControlSetup(driveControl, visionControl, 5));
                break;
            case Talon:
                driveControl = buildTalonDriveControl(self);
                odometryControl = self.childSetup("odo-control", new NavXOdometryControlSetup(driveControl, visionControl));
                break;
            case Sim:
                driveControl = buildSimDriveControl(self);
                odometryControl = self.childSetup("odo-control", new PidgeonOdometryControlSetup(driveControl, visionControl, 5));
                break;
            default:
                throw new RuntimeException("Invalid drive mode: " + mode);
        }

        autoSubsystem.setDriveControl(driveControl);
        autoSubsystem.setOdometryControl(odometryControl);

        HoloControl holoControl = self.childSetup("holo-control",new HoloControlSetup(driveControl, visionControl, odometryControl));

        IValueTuner<DriveSubsystem.OrientationChooser> orientationTuner = self
                .generateTuner(ITuneAs.ENUM(DriveSubsystem.OrientationChooser.class), "set-orientation", DriveSubsystem.OrientationChooser.FIELD_ORIENTED);

        return new DriveSubsystem(
                operatorInput,
                MockingUtil.buddy(IOdometryControl.class),
                balanceControl,
                driveControl,
                autoSubsystem,
                MockingUtil.buddy(HoloControl.class),
                visionControl,
                orientationTuner,
                self.getDebuggable()

        );

    }


    DriveControl buildNeoDriveControl(IProcess path) {
        // used to configure the spark motor in SparkSetup


        DriveControlSetup driveControl = new DriveControlSetup(

                new SwerveModuleSetup(
                        new DriveControllerSetup(
                                new SparkDriveMotorSetup(
                                        DriveConstants.FRONT_LEFT_DRIVE_ID,
                                        DriveConstants.DRIVE_CONFIG,
                                        DriveConstants.MK4I_L2
                                )
                        ),
                        new SteerControllerSetup(
                                new SparkDriveMotorSetup(
                                        DriveConstants.FRONT_LEFT_STEER_ID,
                                        DriveConstants.STEER_CONFIG,
                                        DriveConstants.MK4I_L2
                                ),
                                new ThriftyEncoderSetup(
                                        DriveConstants.FRONT_LEFT_ENCODER_CHANNEL,
                                        DriveConstants.FRONT_LEFT_OFFSET
                                )
                        )
                ),
                new SwerveModuleSetup(
                        new DriveControllerSetup(
                                new SparkDriveMotorSetup(
                                        DriveConstants.FRONT_RIGHT_DRIVE_ID,
                                        DriveConstants.DRIVE_CONFIG,
                                        DriveConstants.MK4I_L2
                                )
                        ),
                        new SteerControllerSetup(
                                new SparkDriveMotorSetup(
                                        DriveConstants.FRONT_RIGHT_STEER_ID,
                                        DriveConstants.STEER_CONFIG,
                                        DriveConstants.MK4I_L2
                                ),
                                new ThriftyEncoderSetup(
                                        DriveConstants.FRONT_RIGHT_ENCODER_CHANNEL,
                                        DriveConstants.FRONT_RIGHT_OFFSET
                                )
                        )
                ),
                MockingUtil.noops(ISwerveModule.class),
                MockingUtil.noops(ISwerveModule.class)
        );

        return path.childSetup("drive-control",driveControl);
    }

    DriveControl buildSimDriveControl(IProcess path) {
        // used to configure the spark motor in SparkSetup


        var driveControl = new DriveControlSetup(
                new SwerveModuleSetup(
                        new DriveControllerSetup(new DCSimSetup(DriveTurdSpecific.DRIVE_TURD, Drive.DRIVE_DC_CONFIG, Drive.PI)),
                        new SteerControllerSetup(
                                new DCSimSetup(DriveConstants.STEER_CONFIG, DriveConstants.STEER_MOTOR, DriveConstants.STEER_PID),
                                new ThriftyEncoderSetup(DriveConstants.FRONT_LEFT_ENCODER_CHANNEL, DriveTurdSpecific.FRONT_LEFT_OFFSET_TURD))
                ),
                new SwerveModuleSetup(
                        new DriveControllerSetup(new DCSimSetup(DriveConstants.DRIVE_CONFIG, DriveConstants.DRIVE_MOTOR, DriveConstants.DRIVE_PID)),
                        new SteerControllerSetup(
                                new DCSimSetup(DriveConstants.STEER_CONFIG, DriveConstants.STEER_MOTOR, Drive.STEER_PID),
                                new ThriftyEncoderSetup(DriveConstants.FRONT_RIGHT_ENCODER_CHANNEL, DriveTurdSpecific.FRONT_RIGHT_OFFSET_TURD))
                ),
                new SwerveModuleSetup(
                        new DriveControllerSetup(new DCSimSetup(DriveConstants.DRIVE_CONFIG, DriveConstants.DRIVE_MOTOR, DriveConstants.DRIVE_PID)),
                        new SteerControllerSetup(
                                new DCSimSetup(DriveConstants.STEER_CONFIG, DriveConstants.STEER_MOTOR, Drive.STEER_PID),
                                new ThriftyEncoderSetup(DriveConstants.BACK_LEFT_ENCODER_CHANNEL, DriveTurdSpecific.BACK_LEFT_OFFSET_TURD))
                ),
                new SwerveModuleSetup(
                        new DriveControllerSetup(new DCSimSetup(DriveConstants.DRIVE_CONFIG, DriveConstants.DRIVE_MOTOR, DriveConstants.DRIVE_PID)),
                        new SteerControllerSetup(
                                new DCSimSetup(DriveConstants.STEER_CONFIG, DriveConstants.STEER_MOTOR, Drive.STEER_PID),
                                new ThriftyEncoderSetup(DriveConstants.BACK_RIGHT_ENCODER_CHANNEL, DriveTurdSpecific.BACK_RIGHT_OFFSET_TURD))
                )
        );

        return path.childSetup("drive-control",driveControl);
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



        double sensorPositionCoefficient = 2.0 * Math.PI / 2048 * DriveConstants.MK4_L2.getSteerReduction();

        var l = new DriveControlSetup(
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

        return path.childSetup("drive-control",l);
    }

}
