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

    double HALF_WIDTH = Units.inchesToMeters(18.25 / 2);
    double HALF_BASE = Units.inchesToMeters(20.5 / 2);


    SwerveDriveKinematics KINEMATICS = new SwerveDriveKinematics(
            new Translation2d(HALF_WIDTH, HALF_BASE),
            new Translation2d(HALF_WIDTH, -HALF_BASE),
            new Translation2d(-HALF_WIDTH, HALF_BASE),
            new Translation2d(-HALF_WIDTH, -HALF_BASE)
    );


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
    double BalanceDeadbandDeg = 0;
}