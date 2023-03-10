package org.bitbuckets.arm.sim;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.system.plant.DCMotor;
import edu.wpi.first.wpilibj.simulation.SingleJointedArmSim;
import edu.wpi.first.wpilibj.smartdashboard.MechanismLigament2d;
import org.bitbuckets.arm.config.ArmJointConfig;
import org.bitbuckets.lib.IProcess;
import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.control.PIDConfig;
import org.bitbuckets.lib.hardware.IMotorController;
import org.bitbuckets.lib.hardware.MotorConfig;


public class SimArmSetup implements ISetup<IMotorController> {

    final MotorConfig config;
    final ArmJointConfig armJointConfig;
    final PIDConfig pidConfig;
    final MechanismLigament2d mechanismLigament2d;
    public SimArmSetup(MotorConfig config, ArmJointConfig armJointConfig, PIDConfig pidConfig, MechanismLigament2d mechanismLigament2d) {
        this.config = config;
        this.armJointConfig = armJointConfig;
        this.pidConfig = pidConfig;
        this.mechanismLigament2d = mechanismLigament2d;
    }

    public static double estimateMOI(double lengthMeters, double massKg) {
        return 1.0 / 3.0 * massKg * lengthMeters * lengthMeters;
    }

    @Override
    public SimArm build(IProcess self) {


        SingleJointedArmSim sim = new SingleJointedArmSim(
                DCMotor.getNeo550(2),
                1.0 / config.encoderToMechanismCoefficient * 10.0, //TODO fix this dumb hack
                armJointConfig.length_meters,
                armJointConfig.jointMinAngle_rads,
                armJointConfig.jointMaxAngle_rads,
                armJointConfig.jointMass_kg,
                armJointConfig.isGravitySimulated
        );

        PIDController pidController = new PIDController(
                pidConfig.kP, pidConfig.kI, pidConfig.kD
        );

        return new SimArm(self.getDebuggable(), mechanismLigament2d, config, sim, pidController);
    }
}
