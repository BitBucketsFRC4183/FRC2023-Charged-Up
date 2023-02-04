package org.bitbuckets.lib.vendor.sim.dc;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.system.plant.DCMotor;
import edu.wpi.first.wpilibj.simulation.DCMotorSim;
import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.ProcessPath;
import org.bitbuckets.lib.control.PIDConfig;
import org.bitbuckets.lib.hardware.IMotorController;
import org.bitbuckets.lib.hardware.MotorConfig;

/**
 * Generic simulator for a motor
 */
public class DCSimSetup implements ISetup<IMotorController> {

    final MotorConfig config;
    final DCMotorConfig dcMotorConfig;
    final PIDConfig pidConfig;

    public DCSimSetup(MotorConfig config, DCMotorConfig dcMotorConfig, PIDConfig pidConfig) {
        this.config = config;
        this.dcMotorConfig = dcMotorConfig;
        this.pidConfig = pidConfig;
    }

    @Override
    public IMotorController build(ProcessPath path) {
        DCMotorSim motorSim = new DCMotorSim(DCMotor.getNeo550(1), 1.0 / config.mechanismCoefficient, dcMotorConfig.momentOfInertia, dcMotorConfig.stdDevs);//TODO reverse, because for us numbers greater than one represent upgear not reduc)
        PIDController pidController = new PIDController(pidConfig.kP, pidConfig.kI, pidConfig.kD);
        DCSimController DCSimController = new DCSimController(config, motorSim, pidController);

        path.registerSimLoop(DCSimController, "dc-motor-sim");

        return DCSimController;
    }
}
