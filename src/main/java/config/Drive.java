package config;

import edu.wpi.first.math.Matrix;
import edu.wpi.first.math.Nat;
import org.bitbuckets.lib.control.PIDConfig;
import org.bitbuckets.lib.vendor.sim.dc.DCMotorConfig;

import java.util.Optional;

public interface Drive {

    PIDConfig DRIVE_SIM_PID = new PIDConfig(
            0,
            0,
            0,
            Optional.empty(),
            Optional.empty()
    );

    PIDConfig STEER_PID = new PIDConfig(
            0,
            0,
            0,
            Optional.empty(),
            Optional.empty()
    );

    PIDConfig STEER_SIM_PID = new PIDConfig(
            0,
            0,
            0,
            Optional.empty(),
            Optional.empty()
    );

    DCMotorConfig DRIVE_DC_CONFIG = new DCMotorConfig(
            0.025,
            Matrix.mat(
                    Nat.N1(),
                    Nat.N1()
            ).fill(0)
    );
    DCMotorConfig STEER_DC_CONFIG = new DCMotorConfig(0.025, Matrix.mat(Nat.N1(), Nat.N1()).fill(0));
}
