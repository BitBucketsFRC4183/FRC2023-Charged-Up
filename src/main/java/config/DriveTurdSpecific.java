package config;

import org.bitbuckets.drive.controlsds.sds.SwerveModuleConfiguration;
import org.bitbuckets.lib.hardware.MotorConfig;

import java.util.Optional;

public interface DriveTurdSpecific {
    double FRONT_LEFT_OFFSET_TURD = Math.toRadians(76.904289); // set front left steer offset
    double FRONT_RIGHT_OFFSET_TURD = Math.toRadians(94.75707037500001); // set front right steer offset
    double BACK_LEFT_OFFSET_TURD = Math.toRadians(2.1972654); // set back left steer offset
    double BACK_RIGHT_OFFSET_TURD = Math.toRadians(119.56785885000002); // set back right steer offset
    SwerveModuleConfiguration MK4I_L2 = new SwerveModuleConfiguration(
            0.10033,
            (14.0 / 50.0) * (27.0 / 17.0) * (15.0 / 45.0),
            true,
            (14.0 / 50.0) * (10.0 / 60.0),
            false
    );
    MotorConfig DRIVE_TURD = new MotorConfig(
            (14.0 / 50.0) * (27.0 / 17.0) * (15.0 / 45.0),
            1,
            0.10033 * Math.PI,
            true,
            true,
            20,
            Optional.empty(),
            Optional.empty(),
            false,
            false,
            Optional.empty()
    );
    MotorConfig STEER_TURD = new MotorConfig(
            (14.0 / 50.0) * (10.0 / 60.0),
            1,
            Math.PI * 0.10033,
            true,
            true,
            20,
            Optional.empty(),
            Optional.empty(),
            false,
            false,
            Optional.empty()
    );
}
