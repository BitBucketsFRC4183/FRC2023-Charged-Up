package org.bitbuckets.lib.vendor.sim.dc;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.system.plant.DCMotor;
import edu.wpi.first.wpilibj.simulation.DCMotorSim;
import org.bitbuckets.lib.IProcess;
import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.control.PIDConfig;
import org.bitbuckets.lib.hardware.IMotorController;
import org.bitbuckets.lib.hardware.MotorConfig;

/**
 * Generic simulator for a motor
 */
public class DCSimSetup implements ISetup<IMotorController> {

    final MotorConfig config;
    final SimInertiaConfig simInertiaConfig;
    final PIDConfig pidConfig;

    public DCSimSetup(MotorConfig config, SimInertiaConfig simInertiaConfig, PIDConfig pidConfig) {
        this.config = config;
        this.simInertiaConfig = simInertiaConfig;
        this.pidConfig = pidConfig;
    }

    @Override
    public IMotorController build(IProcess self) {
        DCMotorSim motorSim = new DCMotorSim(config.motorType, 1.0/ config.encoderToMechanismCoefficient, simInertiaConfig.momentOfInertia);
        PIDController pidController = new PIDController(pidConfig.kP, pidConfig.kI, pidConfig.kD);

        return new DCSimController(config, motorSim, pidController, self.getDebuggable());
    }
}
