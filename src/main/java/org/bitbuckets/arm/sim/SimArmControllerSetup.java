package org.bitbuckets.arm.sim;

import org.bitbuckets.lib.IProcess;
import org.bitbuckets.lib.ISetup;

public class SimArmControllerSetup implements ISetup<SimArmController> {

    final ISetup<SimArmCore> core;

    public SimArmControllerSetup(ISetup<SimArmCore> core) {
        this.core = core;
    }

    @Override
    public SimArmController build(IProcess self) {
        var simArmCore = self.childSetup("core", core);

        simArmCore.
    }
}
