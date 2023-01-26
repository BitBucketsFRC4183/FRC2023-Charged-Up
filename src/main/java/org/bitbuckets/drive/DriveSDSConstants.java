package org.bitbuckets.drive;


import edu.wpi.first.math.controller.SimpleMotorFeedforward;
import org.bitbuckets.drive.controlsds.sds.ModuleConfiguration;
import org.bitbuckets.robot.RobotConstants;

public interface DriveSDSConstants {

    // Drive Subsystem
    int frontLeftModuleDriveMotor_ID = 5;
    int frontLeftModuleSteerMotor_ID = 4;
    int frontLeftModuleSteerEncoder_CHANNEL = 1;

    int frontRightModuleDriveMotor_ID = 6;
    int frontRightModuleSteerMotor_ID = 7;
    int frontRightModuleSteerEncoder_CHANNEL = 2;

    int backLeftModuleDriveMotor_ID = 1;
    int backLeftModuleSteerMotor_ID = 2;
    int backLeftModuleSteerEncoder_CHANNEL = 3;

    int backRightModuleDriveMotor_ID = 8;
    int backRightModuleSteerMotor_ID = 10;
    int backRightModuleSteerEncoder_CHANNEL = 4;

    double drivetrainTrackWidth_meters = 0.6096; // set trackwidth

    double drivetrainWheelBase_meters = 0.7112; // set wheelbase

    double frontLeftModuleSteerOffset = -Math.toRadians(232.55); // set front left steer offset

    double frontRightModuleSteerOffset = -Math.toRadians(331.96 - 180); // set front right steer offset

    double backLeftModuleSteerOffset = -Math.toRadians(255.49); // set back left steer offset

    double backRightModuleSteerOffset = -Math.toRadians(70.66 + 180); // set back right steer offset

    SimpleMotorFeedforward feedForward = new SimpleMotorFeedforward(0.65292, 2.3053, 0.37626); //new SimpleMotorFeedforward(0.12817, 2.3423, 0.53114);

    ModuleConfiguration MK4_L2 = new ModuleConfiguration(
            0.10033,
            (14.0 / 50.0) * (27.0 / 17.0) * (15.0 / 45.0),
            true,
            (15.0 / 32.0) * (10.0 / 60.0),
            true
    );

    ModuleConfiguration MK4I_L2 = new ModuleConfiguration(
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

    int canCoderPeriodMilliseconds = 100;


    String kDriveFeedForwardSKey = "DriveTrain.pidFeedforward.kS";
    String kDriveFeedForwardVKey = "DriveTrain.pidFeedforward.kV";
    String kDriveFeedForwardAKey = "DriveTrain.pidFeedforward.kA";
    double kDriveFeedForwardS = 0.65292;
    double kDriveFeedForwardV = 2.3053;
    double kDriveFeedForwardA = 0.37626;


    String kOrientPKey = "AutoOrient.pid.kP";
    String kOrientIKey = "AutoOrient.pid.kI";
    String kOrientDKey = "AutoOrient.pid.kD";

    String kOrientFKey = "AutoOrient.pid.kF";

    double kOrientkP = 2.4;
    double kOrientkI = 0;
    double kOrientkD = 0;

    double kOrientkF = 0.73;


    double MAX_DRIVE_VELOCITY = 6380.0 / 60.0 * (14.0 / 50.0) * (27.0 / 17.0) * (15.0 / 45.0) * 0.10033 * Math.PI;

    double MAX_ANG_VELOCITY = MAX_DRIVE_VELOCITY / Math.hypot(RobotConstants.WIDTH, RobotConstants.BASE);


    final String kBalancePKey = "AutoBalance.pid.kP";
    final String kBalanceIKey = "AutoBalance.pid.kI";
    final String kBalanceDKey = "AutoBalance.pid.kD";
    double kBalancekP = -0.015;
    double kBalancekI = 0.0;
    double kBalancekD = 0.01;
    final String autoBalanceDeadbandDegKey = "AutoBalance.deadbandDeg";
    double BalanceDeadbandDeg = 6;

}