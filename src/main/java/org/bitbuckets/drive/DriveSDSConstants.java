package org.bitbuckets.drive;


public interface DriveSDSConstants {

    // Drive Subsystem
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

    double drivetrainTrackWidth_meters = 0.6096; // set trackwidth

    double drivetrainWheelBase_meters = 0.7112; // set wheelbase

    double frontLeftModuleSteerOffset = -Math.toRadians(232.55); // set front left steer offset

    double frontRightModuleSteerOffset = -Math.toRadians(331.96 - 180); // set front right steer offset

    double backLeftModuleSteerOffset = -Math.toRadians(255.49); // set back left steer offset

    double backRightModuleSteerOffset = -Math.toRadians(70.66 + 180); // set back right steer offset


}