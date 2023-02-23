package org.bitbuckets.drive;


import edu.wpi.first.math.Matrix;
import edu.wpi.first.math.Nat;
import edu.wpi.first.math.controller.SimpleMotorFeedforward;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.math.util.Units;
import org.bitbuckets.drive.controlsds.sds.SwerveModuleConfiguration;
import org.bitbuckets.lib.control.PIDConfig;
import org.bitbuckets.lib.hardware.MotorConfig;
import org.bitbuckets.lib.vendor.sim.dc.DCMotorConfig;

import java.util.Optional;

public interface DriveConstants {

    double WHEEL_DIAMETER_METERS = 0.10033;
    double WHEEL_CIRCUMFERENCE_METERS = WHEEL_DIAMETER_METERS * Math.PI;

    MotorConfig DRIVE_CONFIG = new MotorConfig(
            (14.0 / 50.0) * (27.0 / 17.0) * (15.0 / 45.0),
            1,
            WHEEL_CIRCUMFERENCE_METERS,
            true,
            true,
            20,
            Optional.empty(),
            Optional.empty(),
            false,
            false,
            Optional.empty()
    );

    MotorConfig STEER_CONFIG = new MotorConfig(
            (14.0 / 50.0) * (10.0 / 60.0),
            1,
            Math.PI * WHEEL_DIAMETER_METERS,
            true,
            true,
            20,
            Optional.empty(),
            Optional.empty(),
            false,
            false,
            Optional.empty()
    );

    PIDConfig DRIVE_PID = new PIDConfig(0, 0, 0, 0);
    PIDConfig STEER_PID = new PIDConfig(1, 0, 0.1, 0);

    DCMotorConfig DRIVE_MOTOR = new DCMotorConfig(0.025, Matrix.mat(Nat.N1(), Nat.N1()).fill(0));
    DCMotorConfig STEER_MOTOR = new DCMotorConfig(0.005, Matrix.mat(Nat.N1(), Nat.N1()).fill(0));

    double HALF_WIDTH = Units.inchesToMeters(18.25 / 2);
    double HALF_BASE = Units.inchesToMeters(20.5 / 2);


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

    double FRONT_LEFT_OFFSET = Math.toRadians(76.904289); // set front left steer offset
    double FRONT_RIGHT_OFFSET = Math.toRadians(94.75707037500001); // set front right steer offset
    double BACK_LEFT_OFFSET = Math.toRadians(2.1972654); // set back left steer offset
    double BACK_RIGHT_OFFSET = Math.toRadians(119.56785885000002); // set back right steer offset

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

    String autoBalanceDeadbandDegKey = "AutoBalance.deadbandDeg";
    double BalanceDeadbandDeg = 6;
}