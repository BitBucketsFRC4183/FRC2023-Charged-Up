package org.bitbuckets.arm;

import config.Arm;
import edu.wpi.first.math.*;
import edu.wpi.first.math.numbers.N1;
import edu.wpi.first.math.numbers.N2;
import edu.wpi.first.math.numbers.N3;
import edu.wpi.first.math.numbers.N4;
import edu.wpi.first.math.system.NumericalIntegration;
import org.bitbuckets.arm.config.ArmJointConfig;

/**
 * Converts between the system state and motor voltages for a double jointed arm.
 *
 * <p>https://www.chiefdelphi.com/t/whitepaper-two-jointed-arm-dynamics/423060
 *
 * <p>https://www.chiefdelphi.com/t/double-jointed-arm-physics-control-simulator/424307
 */
public class ArmDynamics {

    final ArmJointConfig shoulder;
    final ArmJointConfig elbow;

    public ArmDynamics(ArmJointConfig shoulder, ArmJointConfig elbow) {
        this.shoulder = shoulder;
        this.elbow = elbow;
    }


    /**
     * Calculates the joint voltages based on the full joint states as a matrix (feedforward). The
     * rows represent each joint and the columns represent position, velocity, and acceleration.
     */
    public Vector<N2> feedforward(Matrix<N2, N3> state) {
        return feedforward(
                new Vector<>(state.extractColumnVector(0)),
                new Vector<>(state.extractColumnVector(1)),
                new Vector<>(state.extractColumnVector(2)));
    }

    /** Calculates the joint voltages based on the joint positions (feedforward). */
    public Vector<N2> feedforward(Vector<N2> position) {
        return feedforward(position, VecBuilder.fill(0.0, 0.0), VecBuilder.fill(0.0, 0.0));
    }

    /** Calculates the joint voltages based on the joint positions and velocities (feedforward). */
    public Vector<N2> feedforward(Vector<N2> position, Vector<N2> velocity) {
        return feedforward(position, velocity, VecBuilder.fill(0.0, 0.0));
    }

    /** Calculates the joint voltages based on the full joint states as vectors (feedforward). */
    public Vector<N2> feedforward(Vector<N2> position, Vector<N2> velocity, Vector<N2> acceleration) {
        var torque =
                M(position)
                        .times(acceleration)
                        .plus(C(position, velocity).times(velocity))
                        .plus(Tg(position));

        return VecBuilder.fill(
                shoulder.gearbox.getVoltage(torque.get(0, 0), velocity.get(0, 0)),
                elbow.gearbox.getVoltage(torque.get(1, 0), velocity.get(1, 0)));
    }

    /**
     * Adjusts the simulated state of the arm based on applied voltages.
     *
     * @param state The current state of the arm as (position_0, position_1, velocity_0, velocity_1)
     * @param voltage The applied voltage of each joint.
     * @param dt The step length in seconds.
     * @return The new state of the arm as (position_0, position_1, velocity_0, velocity_1)
     */
    public Vector<N4> simulate(Vector<N4> state, Vector<N2> voltage, double dt) {
        return new Vector<>(
                NumericalIntegration.rkdp(
                        (Matrix<N4, N1> x, Matrix<N2, N1> u) -> {
                            // x = current state, u = voltages, return = state derivatives

                            // Get vectors from state
                            var position = VecBuilder.fill(x.get(0, 0), x.get(1, 0));
                            var velocity = VecBuilder.fill(x.get(2, 0), x.get(3, 0));

                            // Calculate torque
                            var shoulderTorque =
                                    shoulder.gearbox.getTorque(
                                            shoulder.gearbox.getCurrent(
                                                    velocity.get(0, 0), 
                                                    u.get(0, 0)
                                            )
                                    );
                            var elbowTorque =
                                    elbow.gearbox.getTorque(
                                                    elbow.gearbox.getCurrent(
                                                            velocity.get(1, 0), 
                                                            u.get(1, 0)
                                                    )
                                    );
                            var torque = VecBuilder.fill(shoulderTorque, elbowTorque);

                            // Apply limits
                            if (position.get(0, 0) < shoulder.jointMinAngle_rads) {
                                position.set(0, 0, shoulder.jointMinAngle_rads);
                                if (velocity.get(0, 0) < 0.0) {
                                    velocity.set(0, 0, 0.0);
                                }
                                if (torque.get(0, 0) < 0.0) {
                                    torque.set(0, 0, 0.0);
                                }
                            }
                            if (position.get(0, 0) > shoulder.jointMaxAngle_rads) {
                                position.set(0, 0, shoulder.jointMaxAngle_rads);
                                if (velocity.get(0, 0) > 0.0) {
                                    velocity.set(0, 0, 0.0);
                                }
                                if (torque.get(0, 0) > 0.0) {
                                    torque.set(0, 0, 0.0);
                                }
                            }
                            if (position.get(1, 0) < elbow.jointMinAngle_rads) {
                                position.set(1, 0, elbow.jointMinAngle_rads);
                                if (velocity.get(1, 0) < 0.0) {
                                    velocity.set(1, 0, 0.0);
                                }
                                if (torque.get(1, 0) < 0.0) {
                                    torque.set(1, 0, 0.0);
                                }
                            }
                            if (position.get(1, 0) > elbow.jointMaxAngle_rads) {
                                position.set(1, 0, elbow.jointMaxAngle_rads);
                                if (velocity.get(1, 0) > 0.0) {
                                    velocity.set(1, 0, 0.0);
                                }
                                if (torque.get(1, 0) > 0.0) {
                                    torque.set(1, 0, 0.0);
                                }
                            }

                            // Calculate acceleration
                            var acceleration =
                                    M(position)
                                            .inv()
                                            .times(
                                                    torque.minus(C(position, velocity).times(velocity)).minus(Tg(position)));

                            // Return state vector
                            return new MatBuilder<>(Nat.N4(), Nat.N1())
                                    .fill(
                                            velocity.get(0, 0),
                                            velocity.get(1, 0),
                                            acceleration.get(0, 0),
                                            acceleration.get(1, 0));
                        },
                        state,
                        voltage,
                        dt));
    }

    private Matrix<N2, N2> M(Vector<N2> position) {
        var M = new Matrix<>(N2.instance, N2.instance);
        M.set(
                0,
                0,
                shoulder.jointMass_kg * Math.pow(shoulder.cgRadius_meters, 2.0)
                        + elbow.jointMass_kg * (Math.pow(shoulder.length_meters, 2.0) + Math.pow(elbow.cgRadius_meters, 2.0))
                        + shoulder.angularInertia_kgm2
                        + elbow.angularInertia_kgm2
                        + 2
                        * elbow.jointMass_kg
                        * shoulder.length_meters
                        * elbow.cgRadius_meters
                        * Math.cos(position.get(1, 0)));
        M.set(
                1,
                0,
                elbow.jointMass_kg * Math.pow(elbow.cgRadius_meters, 2.0)
                        + elbow.angularInertia_kgm2
                        + elbow.jointMass_kg * shoulder.length_meters * elbow.cgRadius_meters * Math.cos(position.get(1, 0)));
        M.set(
                0,
                1,
                elbow.jointMass_kg * Math.pow(elbow.cgRadius_meters, 2.0)
                        + elbow.angularInertia_kgm2
                        + elbow.jointMass_kg * shoulder.length_meters * elbow.cgRadius_meters * Math.cos(position.get(1, 0)));
        M.set(1, 1, elbow.jointMass_kg * Math.pow(elbow.cgRadius_meters, 2.0) + elbow.angularInertia_kgm2);
        return M;
    }

    private Matrix<N2, N2> C(Vector<N2> position, Vector<N2> velocity) {
        var C = new Matrix<>(N2.instance, N2.instance);
        C.set(
                0,
                0,
                -elbow.jointMass_kg
                        * shoulder.length_meters
                        * elbow.cgRadius_meters
                        * Math.sin(position.get(1, 0))
                        * velocity.get(1, 0));
        C.set(
                1,
                0,
                elbow.jointMass_kg
                        * shoulder.length_meters
                        * elbow.cgRadius_meters
                        * Math.sin(position.get(1, 0))
                        * velocity.get(0, 0));
        C.set(
                0,
                1,
                -elbow.jointMass_kg
                        * shoulder.length_meters
                        * elbow.cgRadius_meters
                        * Math.sin(position.get(1, 0))
                        * (velocity.get(0, 0) + velocity.get(1, 0)));
        return C;
    }

    private Matrix<N2, N1> Tg(Vector<N2> position) {
        var Tg = new Matrix<>(N2.instance, N1.instance);
        Tg.set(
                0,
                0,
                (shoulder.jointMass_kg * shoulder.cgRadius_meters + elbow.jointMass_kg * shoulder.length_meters)
                        * Arm.GRAVITY_MSS
                        * Math.cos(position.get(0, 0))
                        + elbow.jointMass_kg
                        * elbow.cgRadius_meters
                        * Arm.GRAVITY_MSS
                        * Math.cos(position.get(0, 0) + position.get(1, 0)));
        Tg.set(
                1,
                0,
                elbow.jointMass_kg * elbow.cgRadius_meters * Arm.GRAVITY_MSS * Math.cos(position.get(0, 0) + position.get(1, 0)));
        return Tg;
    }
}
