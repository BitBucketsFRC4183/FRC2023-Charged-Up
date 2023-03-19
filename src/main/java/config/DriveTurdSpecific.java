package config;

import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.system.plant.DCMotor;
import edu.wpi.first.math.util.Units;
import org.bitbuckets.drive.controlsds.sds.SwerveModuleConfiguration;
import org.bitbuckets.lib.hardware.MotorConfig;
import org.bitbuckets.lib.hardware.OptimizationMode;

import java.util.Optional;

public interface DriveTurdSpecific {
    double FRONT_LEFT_OFFSET_TURD = Math.toRadians(76.904289); // set front left steer offset
    double FRONT_RIGHT_OFFSET_TURD = Math.toRadians(94.75707037500001); // set front right steer offset
    double BACK_LEFT_OFFSET_TURD = Math.toRadians(2.1972654); // set back left steer offset
    double BACK_RIGHT_OFFSET_TURD = Math.toRadians(119.56785885000002); // set back right steer offset

    double HALF_WIDTH = Units.inchesToMeters(18.25 / 2);
    double HALF_BASE = Units.inchesToMeters(20.5 / 2);

    /**
     * The location of the swerve modules compared to the center of the robot
     * <p>
     * BL ----- FL
     * |        |
     * |    C   | ----> forward x axis
     * |        |
     * BR ----- FR
     */
    SwerveDriveKinematics KINEMATICS = new SwerveDriveKinematics(
            new Translation2d(HALF_WIDTH, HALF_BASE),
            new Translation2d(HALF_WIDTH, -HALF_BASE),
            new Translation2d(-HALF_WIDTH, HALF_BASE),
            new Translation2d(-HALF_WIDTH, -HALF_BASE)
    );
    SwerveModuleConfiguration MK4I_L2 = new SwerveModuleConfiguration(
            0.10033,
            (14.0 / 50.0) * (27.0 / 17.0) * (15.0 / 45.0),
            true,
            (14.0 / 50.0) * (10.0 / 60.0),
            false
    );

    MotorConfig DRIVE_TURD = new MotorConfig(
            (14.0 / 50.0) * (27.0 / 17.0) * (15.0 / 45.0), //1 / 6.75
            1,
            0.10033 * Math.PI,
            true,
            false,
            80,
            Optional.empty(),
            Optional.empty(),
            false,
            false,
            true, OptimizationMode.VOLTAGE,
            DCMotor.getNEO(1),
            false
    );
    MotorConfig STEER_TURD = new MotorConfig(
            (14.0 / 50.0) * (10.0 / 60.0),
            1,
            Math.PI * 0.10033,
            true,
            false,
            20,
            Optional.empty(),
            Optional.empty(),
            false,
            false,
            true, OptimizationMode.VOLTAGE,
            DCMotor.getNeo550(1),
            false
    );
}
