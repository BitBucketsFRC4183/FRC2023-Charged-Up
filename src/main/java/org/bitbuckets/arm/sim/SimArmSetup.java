package org.bitbuckets.arm.sim;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.system.plant.DCMotor;
import edu.wpi.first.wpilibj.simulation.SingleJointedArmSim;
import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.ProcessPath;
import org.bitbuckets.lib.control.PIDConfig;
import org.bitbuckets.lib.hardware.IMotorController;
import org.bitbuckets.lib.hardware.MotorConfig;


public class SimArmSetup implements ISetup<IMotorController> {

    final MotorConfig config;
    final ArmConfig armConfig;
    final PIDConfig pidConfig;

    public SimArmSetup(MotorConfig config, ArmConfig armConfig, PIDConfig pidConfig) {
        this.config = config;
        this.armConfig = armConfig;
        this.pidConfig = pidConfig;
    }

    public static double estimateMOI(double lengthMeters, double massKg) {
        return 1.0 / 3.0 * massKg * lengthMeters * lengthMeters;
    }

    @Override
    public SimArm build(ProcessPath self) {

        SingleJointedArmSim sim = new SingleJointedArmSim(
                DCMotor.getNeo550(1),
                config.encoderToMechanismCoefficient,
                estimateMOI(armConfig.lengthMeters, armConfig.armMass),
                armConfig.lengthMeters,
                armConfig.armMinAngle,
                armConfig.armMaxAngle,
                armConfig.armMass,
                armConfig.isGravitySimulated
        );

        PIDController pidController = new PIDController(
                pidConfig.kP, pidConfig.kI, pidConfig.kD
        );

        return new SimArm(config, sim, pidController);
    }
}
