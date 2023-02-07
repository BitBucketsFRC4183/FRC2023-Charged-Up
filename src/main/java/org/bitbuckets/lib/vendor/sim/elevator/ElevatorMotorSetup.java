package org.bitbuckets.lib.vendor.sim.elevator;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.system.plant.DCMotor;
import edu.wpi.first.wpilibj.simulation.ElevatorSim;
import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.control.PIDConfig;
import org.bitbuckets.lib.hardware.IMotorController;
import org.bitbuckets.lib.hardware.MotorConfig;
import org.bitbuckets.lib.ProcessPath;

public class ElevatorMotorSetup implements ISetup<IMotorController> {

    final MotorConfig motorConfig;
    final PIDConfig pidConfig;
    final ElevatorConfig elevatorConfig;

    public ElevatorMotorSetup(MotorConfig motorConfig, PIDConfig pidConfig, ElevatorConfig elevatorConfig) {
        this.motorConfig = motorConfig;
        this.pidConfig = pidConfig;
        this.elevatorConfig = elevatorConfig;
    }

    @Override
    public IMotorController build(ProcessPath path) {
        ElevatorSim elevatorSim = new ElevatorSim(
                DCMotor.getNeo550(1),
                1.0 / motorConfig.mechanismCoefficient,
                elevatorConfig.carriageMassKg,
                elevatorConfig.drumRadius,
                elevatorConfig.minHeight,
                elevatorConfig.maxHeight,
                true,
                elevatorConfig.stdDevs
        );

        PIDController controller = new PIDController(pidConfig.kP, pidConfig.kI, pidConfig.kD);

        ElevatorSimController simController = new ElevatorSimController(
                elevatorSim,
                controller,
                motorConfig
        );

        path.registerLoop(simController, 20, "elevator-loop");


        return simController;
    }
}
