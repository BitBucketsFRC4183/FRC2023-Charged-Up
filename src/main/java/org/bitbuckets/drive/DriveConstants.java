package org.bitbuckets.drive;


import edu.wpi.first.math.controller.SimpleMotorFeedforward;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import org.bitbuckets.drive.controlsds.sds.SwerveModuleConfiguration;
import org.bitbuckets.robot.RobotConstants;

public interface DriveConstants {

    //TODO divide by 2
    double SENSOR_UNITS_PER_REVOLUTION = 2048.0;

    double TURN_REDUCTION = (15.0 / 32.0) * (10.0 / 60.0);
    double DRIVE_REDUCTION = (14.0 / 50.0) * (27.0 / 17.0) * (15.0 / 45.0);
    double DRIVE_METERS_FACTOR = 1;
    double TURN_METERS_FACTOR = 1; //TODO fix

    double MAX_DRIVE_VELOCITY = 6380.0 / 60.0 * (14.0 / 50.0) * (27.0 / 17.0) * (15.0 / 45.0) * 0.10033 * Math.PI;
    double MAX_ANG_VELOCITY = MAX_DRIVE_VELOCITY / Math.hypot(RobotConstants.WIDTH, RobotConstants.BASE);

    //TODO get rid of this
    SimpleMotorFeedforward FF = new SimpleMotorFeedforward(0.65292, 2.3053, 0.37626); //converts velocity to voltage

    SwerveModuleState[] LOCK = new SwerveModuleState[]{
            new SwerveModuleState(0, Rotation2d.fromDegrees(45)),
            new SwerveModuleState(0, Rotation2d.fromDegrees(-45)),
            new SwerveModuleState(0, Rotation2d.fromDegrees(-45)),
            new SwerveModuleState(0, Rotation2d.fromDegrees(45))
    };


    // Drive Subsystem
    int frontLeftModuleDriveMotor_ID = 5;
    int frontLeftModuleSteerMotor_ID = 6;
    int frontLeftModuleSteerEncoder_CHANNEL = 2;

    int frontRightModuleDriveMotor_ID = 7;
    int frontRightModuleSteerMotor_ID = 8;
    int frontRightModuleSteerEncoder_CHANNEL = 3;

    int backLeftModuleDriveMotor_ID = 3;
    int backLeftModuleSteerMotor_ID = 4;
    int backLeftModuleSteerEncoder_CHANNEL = 1;

    int backRightModuleDriveMotor_ID = 1;
    int backRightModuleSteerMotor_ID = 2;
    int backRightModuleSteerEncoder_CHANNEL = 0;

    double drivetrainTrackWidth_meters = 0.6096; // set trackwidth

    double drivetrainWheelBase_meters = 0.7112; // set wheelbase

    double frontLeftModuleSteerOffset = Math.toRadians(76.904289); // set front left steer offset

    double frontRightModuleSteerOffset = Math.toRadians(94.75707037500001); // set front right steer offset

    double backLeftModuleSteerOffset = Math.toRadians(2.1972654); // set back left steer offset

    double backRightModuleSteerOffset = Math.toRadians(119.56785885000002); // set back right steer offset

    SimpleMotorFeedforward feedForward = new SimpleMotorFeedforward(0.65292, 2.3053, 0.37626); //new SimpleMotorFeedforward(0.12817, 2.3423, 0.53114);

    SwerveModuleConfiguration MK4_L2 = new SwerveModuleConfiguration(
            0.10033,
            (14.0 / 50.0) * (27.0 / 17.0) * (15.0 / 45.0),
            true,
            (15.0 / 32.0) * (10.0 / 60.0),
            true
    );

    SwerveModuleConfiguration MK4I_L2 = new SwerveModuleConfiguration(
            0.10033,
            (14.0 / 50.0) * (27.0 / 17.0) * (15.0 / 45.0),
            true,
            (14.0 / 50.0) * (10.0 / 60.0),
            false
    );

    int CAN_TIMEOUT_MS = 250;
    int STATUS_FRAME_GENERAL_PERIOD_MS = 250;
    double TICKS_PER_ROTATION = 2048.0;

    double nominalVoltage = 12.0;
    double driveCurrentLimit = 80.0;
    double steerCurrentLimit = 20.0;

    double proportionalConstant = 1.0;
    double integralConstant = 0;
    double derivativeConstant = 0.1;
    String autoBalanceDeadbandDegKey = "AutoBalance.deadbandDeg";
    double BalanceDeadbandDeg = 6;
}