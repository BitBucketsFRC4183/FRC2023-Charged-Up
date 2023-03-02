package config;

import edu.wpi.first.math.system.plant.DCMotor;
import org.bitbuckets.drive.controlsds.sds.SwerveModuleConfiguration;
import org.bitbuckets.lib.hardware.MotorConfig;
import org.bitbuckets.lib.hardware.OptimizationMode;

import java.util.Optional;

public interface DriveAppaSpecific {

    MotorConfig STEER_APPA = new MotorConfig(
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
            OptimizationMode.GENERIC,
            DCMotor.getFalcon500(1).withReduction(1) //TODO fix for sim
    );

    double FRONT_LEFT_OFFSET_APPA = -Math.toRadians(51.6); // set front left steer offset
    double FRONT_RIGHT_OFFSET_APPA = -Math.toRadians(146.7 - 180); // set front right steer offset
    double BACK_LEFT_OFFSET_APPA = -Math.toRadians(254.9 + 180); // set back left steer offset
    double BACK_RIGHT_OFFSET_APPA = -Math.toRadians(66.7); // set back right steer offset

    SwerveModuleConfiguration MK4_L2 = new SwerveModuleConfiguration(
            0.10033,
            (14.0 / 50.0) * (27.0 / 17.0) * (15.0 / 45.0),
            true,
            (15.0 / 32.0) * (10.0 / 60.0),
            true
    );
}
