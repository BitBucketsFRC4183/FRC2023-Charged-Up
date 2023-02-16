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
    final DCMotorConfig dcMotorConfig;
    final PIDConfig pidConfig;

    public DCSimSetup(MotorConfig config, DCMotorConfig dcMotorConfig, PIDConfig pidConfig) {
        this.config = config;
        this.dcMotorConfig = dcMotorConfig;
        this.pidConfig = pidConfig;
    }

    @Override
    public IMotorController build(IProcess self) {
        double n = 1 / config.encoderToMechanismCoefficient;

        self.getDebuggable().log("the-coefficient", n);

        DCMotorSim motorSim = new DCMotorSim(DCMotor.getNeo550(1), 1.0/ config.encoderToMechanismCoefficient, dcMotorConfig.momentOfInertia);
        PIDController pidController = new PIDController(pidConfig.kP, pidConfig.kI, pidConfig.kD);

        return new DCSimController(config, motorSim, pidController, self.getDebuggable());
    }
}
