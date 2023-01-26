package org.bitbuckets.drive;


import edu.wpi.first.math.controller.SimpleMotorFeedforward;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import org.bitbuckets.robot.RobotConstants;

public interface DriveConstants {

    double TURN_REDUCTION = (15.0 / 32.0) * (10.0 / 60.0);
    double DRIVE_REDUCTION = (14.0 / 50.0) * (27.0 / 17.0) * (15.0 / 45.0);
    double DRIVE_METERS_FACTOR = 1;
    double TURN_METERS_FACTOR = 1; //TODO fix

    double MAX_DRIVE_VELOCITY = 6380.0 / 60.0 * (14.0 / 50.0) * (27.0 / 17.0) * (15.0 / 45.0) * 0.10033 * Math.PI;
    double SLOW_DRIVE_VELOCITY = MAX_DRIVE_VELOCITY * 0.75;
    double MAX_ANG_VELOCITY = MAX_DRIVE_VELOCITY / Math.hypot(RobotConstants.WIDTH, RobotConstants.BASE);

    //TODO get rid of this
    SimpleMotorFeedforward FF = new SimpleMotorFeedforward(0.65292, 2.3053, 0.37626); //converts velocity to voltage

    SwerveModuleState[] LOCK = new SwerveModuleState[]{
            new SwerveModuleState(0, Rotation2d.fromDegrees(45)),
            new SwerveModuleState(0, Rotation2d.fromDegrees(-45)),
            new SwerveModuleState(0, Rotation2d.fromDegrees(-45)),
            new SwerveModuleState(0, Rotation2d.fromDegrees(45))
    };

    SwerveDriveKinematics KINEMATICS = new SwerveDriveKinematics(
            RobotConstants
    );


}