package org.bitbuckets.arm.sim;

import edu.wpi.first.math.VecBuilder;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.system.plant.DCMotor;
import edu.wpi.first.wpilibj.simulation.SingleJointedArmSim;
import edu.wpi.first.wpilibj.smartdashboard.MechanismLigament2d;
import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.ProcessPath;
import org.bitbuckets.lib.control.PIDConfig;
import org.bitbuckets.lib.hardware.IMotorController;
import org.bitbuckets.lib.hardware.MotorConfig;


public class SimArmSetup implements ISetup<IMotorController> {

    final MotorConfig config;
    final ArmConfig armConfig;
    final PIDConfig pidConfig;
    final MechanismLigament2d mechanismLigament2d;

    public SimArmSetup(MotorConfig config, ArmConfig armConfig, PIDConfig pidConfig, MechanismLigament2d mechanismLigament2d) {
        this.config = config;
        this.armConfig = armConfig;
        this.pidConfig = pidConfig;
        this.mechanismLigament2d = mechanismLigament2d;
    }

    public static double estimateMOI(double lengthMeters, double massKg) {
        return 1.0 / 3.0 * massKg * lengthMeters * lengthMeters;
    }

    @Override
    public SimArm build(ProcessPath self) {

        SingleJointedArmSim sim = new SingleJointedArmSim(
                DCMotor.getNeo550(1),
                1.0 / config.encoderToMechanismCoefficient,
                estimateMOI(armConfig.lengthMeters, armConfig.armMass),
                armConfig.lengthMeters,
                armConfig.armMinAngle,
                armConfig.armMaxAngle,
                armConfig.armMass,
                armConfig.isGravitySimulated,
                VecBuilder.fill(0.001)
        );

        PIDController pidController = new PIDController(
                pidConfig.kP, pidConfig.kI, pidConfig.kD
        );

        SimArm arm = new SimArm(self.generateDebugger(), mechanismLigament2d, config, sim, pidController);

        self.registerSimLoop(arm::runSimulationLoop, "simulate-arm");

        return arm;
    }
}
