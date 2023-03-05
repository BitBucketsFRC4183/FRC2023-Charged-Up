package config;

import edu.wpi.first.math.Matrix;
import edu.wpi.first.math.Nat;
import edu.wpi.first.math.VecBuilder;
import edu.wpi.first.math.Vector;
import edu.wpi.first.math.controller.SimpleMotorFeedforward;
import edu.wpi.first.math.numbers.N3;
import edu.wpi.first.math.util.Units;
import org.bitbuckets.lib.control.PIDConfig;
import org.bitbuckets.lib.vendor.sim.dc.SimInertiaConfig;

import java.util.Optional;

public interface Drive {

    double HALF_WIDTH = Units.inchesToMeters(18.25 / 2);
    double HALF_BASE = Units.inchesToMeters(20.5 / 2);

    double MAX_DRIVE_VELOCITY = 6380.0 / 60.0 * (14.0 / 50.0) * (27.0 / 17.0) * (15.0 / 45.0) * 0.10033 * Math.PI;
    double MAX_ANG_VELOCITY = MAX_DRIVE_VELOCITY / Math.hypot(HALF_WIDTH, HALF_BASE); //TODO make it swappable

    PIDConfig DRIVE_SIM_PID = new PIDConfig(
            0,
            0,
            0,
            Optional.empty(),
            Optional.empty()
    );

    PIDConfig STEER_PID = new PIDConfig(
            2,
            0,
            0,
            Optional.empty(),
            Optional.empty()
    );

    PIDConfig STEER_SIM_PID = new PIDConfig(
            2,
            0,
            0,
            Optional.empty(),
            Optional.empty()
    );

    PIDConfig DRIVE_BALANCE_PID = new PIDConfig(
            0.5,
            0,
            0,
            Optional.empty(),
            Optional.empty()
    );

    SimInertiaConfig DRIVE_DC_CONFIG = new SimInertiaConfig(
            0.025,
            Matrix.mat(
                    Nat.N1(),
                    Nat.N1()
            ).fill(0)
    );

    Vector<N3> STD_VISION = VecBuilder.fill(0.5, 0.5, Units.degreesToRadians(10));

    SimInertiaConfig STEER_DC_CONFIG = new SimInertiaConfig(0.025, Matrix.mat(Nat.N1(), Nat.N1()).fill(0));
    SimpleMotorFeedforward FF = new SimpleMotorFeedforward(0.65292, 2.3053, 0.37626); //converts velocity to voltage
}
