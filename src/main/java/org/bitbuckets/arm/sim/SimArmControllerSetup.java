package org.bitbuckets.arm.sim;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj.smartdashboard.MechanismLigament2d;
import org.bitbuckets.lib.IProcess;
import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.control.PIDConfig;
import org.bitbuckets.lib.hardware.IMotorController;

public class SimArmControllerSetup implements ISetup<IMotorController> {

    final ISetup<SimArmCore> core;

    final MechanismLigament2d ligament2d;
    final SimJoint which;
    final PIDConfig config;

    public SimArmControllerSetup(ISetup<SimArmCore> core, MechanismLigament2d ligament2d, SimJoint which, PIDConfig config) {
        this.core = core;
        this.ligament2d = ligament2d;
        this.which = which;
        this.config = config;
    }

    @Override
    public SimArmController build(IProcess self) {
        var simArmCore = self.childSetup("core", core);

        //TODO this is dumb

        return new SimArmController(self.getDebuggable(), which, simArmCore, ligament2d, new PIDController(config.kP, config.kI, config.kD));
    }
}
