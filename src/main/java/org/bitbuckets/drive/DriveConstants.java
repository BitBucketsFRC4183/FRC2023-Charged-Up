package org.bitbuckets.drive;


import edu.wpi.first.math.controller.SimpleMotorFeedforward;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import org.bitbuckets.drive.controlsds.sds.SwerveModuleConfiguration;
import org.bitbuckets.robot.RobotConstants;

public interface DriveConstants {

    double HALF_WIDTH = 0.6096 / 2; //width of bot ?
    double HALF_BASE = 0.7712 / 2; //base of bot ?
    double WHEEL_DIAMETER_METERS = 0.10033;
    double WHEEL_CIRCUMFERENCE_METERS = WHEEL_DIAMETER_METERS * Math.PI;

    SwerveDriveKinematics KINEMATICS = new SwerveDriveKinematics(
            new Translation2d(HALF_WIDTH, HALF_BASE),
            new Translation2d(HALF_WIDTH, -HALF_BASE),
            new Translation2d(-HALF_WIDTH, HALF_BASE),
            new Translation2d(-HALF_WIDTH, -HALF_BASE)
    );

    //TODO divide by 2
    double SENSOR_UNITS_PER_REVOLUTION = 2048.0;

    double TURN_REDUCTION = (15.0 / 32.0) * (10.0 / 60.0);
    double DRIVE_REDUCTION = (14.0 / 50.0) * (27.0 / 17.0) * (15.0 / 45.0);
    double DRIVE_METERS_FACTOR = 1;
    double TURN_METERS_FACTOR = 1; //TODO fix

    double MAX_DRIVE_VELOCITY = 6380.0 / 60.0 * (14.0 / 50.0) * (27.0 / 17.0) * (15.0 / 45.0) * 0.10033 * Math.PI;
    double MAX_ANG_VELOCITY = MAX_DRIVE_VELOCITY / Math.hypot(HALF_WIDTH, HALF_BASE);

    SimpleMotorFeedforward FF = new SimpleMotorFeedforward(0.65292, 2.3053, 0.37626); //converts velocity to voltage

    SwerveModuleState[] LOCK = new SwerveModuleState[]{
            new SwerveModuleState(0, Rotation2d.fromDegrees(45)),
            new SwerveModuleState(0, Rotation2d.fromDegrees(-45)),
            new SwerveModuleState(0, Rotation2d.fromDegrees(-45)),
            new SwerveModuleState(0, Rotation2d.fromDegrees(45))
    };

    int FRONT_LEFT_DRIVE_ID = 5;
    int FRONT_LEFT_STEER_ID = 6;
    int FRONT_LEFT_ENCODER_CHANNEL = 2;

    int FRONT_RIGHT_DRIVE_ID = 7;
    int FRONT_RIGHT_STEER_ID = 8;
    int FRONT_RIGHT_ENCODER_CHANNEL = 3;

    int BACK_LEFT_DRIVE_ID = 3;
    int BACK_LEFT_STEER_ID = 4;
    int BACK_LEFT_ENCODER_CHANNEL = 1;

    int BACK_RIGHT_DRIVE_ID = 1;
    int BACK_RIGHT_STEER_ID = 2;
    int BACK_RIGHT_ENCODER_CHANNEL = 0;

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

    String autoBalanceDeadbandDegKey = "AutoBalance.deadbandDeg";
    double BalanceDeadbandDeg = 6;
}