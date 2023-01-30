package org.bitbuckets.lib.vendor.sim;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.system.plant.DCMotor;
import edu.wpi.first.wpilibj.simulation.DCMotorSim;
import edu.wpi.first.wpilibj.simulation.FlywheelSim;
import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.ProcessPath;
import org.bitbuckets.lib.hardware.IMotorController;
import org.bitbuckets.lib.hardware.MotorConfig;

public class SimMotorSetup implements ISetup<IMotorController> {

    final MotorConfig config;
    final SimulationConfig simulationConfig;


    public SimMotorSetup(MotorConfig config, SimulationConfig simulationConfig) {
        this.config = config;
        this.simulationConfig = simulationConfig;
    }

    @Override
    public IMotorController build(ProcessPath path) {
        FlywheelSim motorSim = new FlywheelSim(DCMotor.getNeo550(1), 1, simulationConfig.simulatedMomentOfInertia);//TODO reverse, because for us numbers greater than one represent upgear not reduc)
        PIDController pidController = new PIDController(simulationConfig.simulatedP, simulationConfig.simulatedI, simulationConfig.simulatedD);
        SimMotorController simMotorController = new SimMotorController(config, motorSim, pidController);

        path.registerLoop(simMotorController, 20, "ass");

        return simMotorController;
    }
}
