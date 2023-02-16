package org.bitbuckets.arm;



import edu.wpi.first.math.MatBuilder;
import edu.wpi.first.math.Matrix;
import edu.wpi.first.math.Nat;
import edu.wpi.first.math.VecBuilder;
import edu.wpi.first.math.Vector;
import edu.wpi.first.math.numbers.N1;
import edu.wpi.first.math.numbers.N2;
import edu.wpi.first.math.numbers.N3;
import edu.wpi.first.math.numbers.N4;
import edu.wpi.first.math.system.NumericalIntegration;
import edu.wpi.first.math.system.plant.DCMotor;
import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.hardware.IMotorController;


public class ArmFeedFordward {

    // lower = shoulder, upper = elbow, grippy = wrist


    private static final double g = 9.80665;
     final ISetup<IMotorController> lowerArm;
     final ISetup<IMotorController> upperArm;











    public ArmFeedFordward(ISetup<IMotorController> lowerArm, ISetup<IMotorController> upperArm) {
        this.lowerArm = lowerArm;
        this.upperArm = upperArm;
    }

    static double getVoltageNowUpper(double torque, double velocity) {
        return DCMotor.getNeo550(1).getVoltage(torque, velocity);

    }

    static double getVoltageNowLower(double torque, double velocity) {
        return DCMotor.getNeo550(2).getVoltage(torque, velocity);
    }

    static double getCurrentNowUpper(double velocity, double voltage) {
          return DCMotor.getNeo550(1).getCurrent(velocity, voltage);
    }

    static double getCurrentNowLower(double velocity, double voltage) {
          return DCMotor.getNeo550(2).getCurrent(velocity, voltage);
    }

    static double getTorqueNowUpper(double current) {
          return DCMotor.getNeo550(1).getTorque(current);
    }

    static double getTorqueNowLower(double current) {
          return DCMotor.getNeo550(2).getTorque(current);
    }






    /** Calculates the joint voltages based on the joint positions (feedforward). */
    public Vector<N2> feedforward(Vector<N2> position) {
        return feedforward(position, VecBuilder.fill(0.0, 0.0), VecBuilder.fill(0.0, 0.0));
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




    /** Calculates the joint voltages based on the full joint states as vectors (feedforward). */
    public Vector<N2> feedforward(Vector<N2> position, Vector<N2> velocity, Vector<N2> acceleration) {
        var torque =
                M(position)
                        .times(acceleration)
                        .plus(C(position, velocity).times(velocity))
                        .plus(Tg(position));
        return VecBuilder.fill(
                getVoltageNowUpper(torque.get(0, 0), velocity.get(0, 0)),
                getVoltageNowLower(torque.get(1, 0), velocity.get(1, 0)));
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
                                    getTorqueNowLower(getCurrentNowLower(velocity.get(0, 0), u.get(0, 0)));
                            var elbowTorque =
                                    getTorqueNowUpper(getCurrentNowUpper(velocity.get(1, 0), u.get(1, 0)));
                            var torque = VecBuilder.fill(shoulderTorque, elbowTorque);

                            // Apply limits
                            if (position.get(0, 0) < ArmConstants.LOWER_ARM.armMinAngle) {
                                position.set(0, 0, ArmConstants.LOWER_ARM.armMinAngle);
                                if (velocity.get(0, 0) < 0.0) {
                                    velocity.set(0, 0, 0.0);
                                }
                                if (torque.get(0, 0) < 0.0) {
                                    torque.set(0, 0, 0.0);
                                }
                            }
                            if (position.get(0, 0) > ArmConstants.LOWER_ARM.armMaxAngle) {
                                position.set(0, 0, ArmConstants.LOWER_ARM.armMaxAngle);
                                if (velocity.get(0, 0) > 0.0) {
                                    velocity.set(0, 0, 0.0);
                                }
                                if (torque.get(0, 0) > 0.0) {
                                    torque.set(0, 0, 0.0);
                                }
                            }
                            if (position.get(1, 0) < ArmConstants.UPPER_ARM.armMinAngle) {
                                position.set(1, 0, ArmConstants.UPPER_ARM.armMinAngle);
                                if (velocity.get(1, 0) < 0.0) {
                                    velocity.set(1, 0, 0.0);
                                }
                                if (torque.get(1, 0) < 0.0) {
                                    torque.set(1, 0, 0.0);
                                }
                            }
                            if (position.get(1, 0) > ArmConstants.UPPER_ARM.armMaxAngle) {
                                position.set(1, 0, ArmConstants.UPPER_ARM.armMaxAngle);
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
                ArmConstants.LOWER_ARM.armMass * Math.pow(ArmConstants.LOWER_CGRADIUS, 2.0)
                        + ArmConstants.FFUPPER_ARM_MASS * (Math.pow(ArmConstants.LOWER_ARM.lengthMeters, 2.0) + Math.pow(ArmConstants.UPPER_CGRADIUS, 2.0))
                        + ArmConstants.LOWER_MOI
                        + ArmConstants.UPPER_MOI
                        + 2
                        * ArmConstants.FFUPPER_ARM_MASS
                        * ArmConstants.LOWER_ARM.lengthMeters
                        * ArmConstants.UPPER_CGRADIUS
                        * Math.cos(position.get(1, 0)));
        M.set(
                1,
                0,
                ArmConstants.FFUPPER_ARM_MASS * Math.pow(ArmConstants.UPPER_CGRADIUS, 2.0)
                        + ArmConstants.UPPER_MOI
                        + ArmConstants.FFUPPER_ARM_MASS * ArmConstants.LOWER_ARM.lengthMeters * ArmConstants.UPPER_CGRADIUS * Math.cos(position.get(1, 0)));
        M.set(
                0,
                1,
                ArmConstants.FFUPPER_ARM_MASS * Math.pow(ArmConstants.UPPER_CGRADIUS, 2.0)
                        + ArmConstants.UPPER_MOI
                        + ArmConstants.FFUPPER_ARM_MASS * ArmConstants.LOWER_ARM.lengthMeters * ArmConstants.UPPER_CGRADIUS * Math.cos(position.get(1, 0)));
        M.set(1, 1, ArmConstants.FFUPPER_ARM_MASS * Math.pow(ArmConstants.UPPER_CGRADIUS, 2.0) + ArmConstants.UPPER_MOI);
        return M;
    }

    private Matrix<N2, N2> C(Vector<N2> position, Vector<N2> velocity) {
        var C = new Matrix<>(N2.instance, N2.instance);
        C.set(
                0,
                0,
                -ArmConstants.FFUPPER_ARM_MASS
                        * ArmConstants.LOWER_ARM.lengthMeters
                        * ArmConstants.UPPER_CGRADIUS
                        * Math.sin(position.get(1, 0))
                        * velocity.get(1, 0));
        C.set(
                1,
                0,
                ArmConstants.FFUPPER_ARM_MASS
                        * ArmConstants.LOWER_ARM.lengthMeters
                        * ArmConstants.UPPER_CGRADIUS
                        * Math.sin(position.get(1, 0))
                        * velocity.get(0, 0));
        C.set(
                0,
                1,
                -ArmConstants.FFUPPER_ARM_MASS
                        * ArmConstants.LOWER_ARM.lengthMeters
                        * ArmConstants.UPPER_CGRADIUS
                        * Math.sin(position.get(1, 0))
                        * (velocity.get(0, 0) + velocity.get(1, 0)));
        return C;
    }

    private Matrix<N2, N1> Tg(Vector<N2> position) {
        var Tg = new Matrix<>(N2.instance, N1.instance);
        Tg.set(
                0,
                0,
                (ArmConstants.LOWER_ARM.armMass * ArmConstants.LOWER_CGRADIUS + ArmConstants.FFUPPER_ARM_MASS * ArmConstants.LOWER_ARM.lengthMeters)
                        *
                        g
                        * Math.cos(position.get(0, 0))
                        + ArmConstants.FFUPPER_ARM_MASS
                        * ArmConstants.UPPER_CGRADIUS
                        * g
                        * Math.cos(position.get(0, 0) + position.get(1, 0)));
        Tg.set(
                1,
                0,
                ArmConstants.FFUPPER_ARM_MASS * ArmConstants.UPPER_CGRADIUS * g * Math.cos(position.get(0, 0) + position.get(1, 0)));
        return Tg;

}
}
