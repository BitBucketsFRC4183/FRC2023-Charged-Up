package org.bitbuckets.arm.sim;

import edu.wpi.first.math.Vector;
import edu.wpi.first.math.numbers.N4;
import org.bitbuckets.arm.ArmDynamics;
import org.bitbuckets.lib.IProcess;
import org.bitbuckets.lib.ISetup;



public class SimArmCoreSetup implements ISetup<SimArmCore> {

    final ArmDynamics dynamics;
    final Vector<N4> shoulderElbowInitialStates;

    public SimArmCoreSetup(ArmDynamics dynamics, Vector<N4> shoulderElbowInitialStates) {
        this.dynamics = dynamics;
        this.shoulderElbowInitialStates = shoulderElbowInitialStates;
    }

    @Override
    public SimArmCore build(IProcess self) {
        return new SimArmCore(dynamics, shoulderElbowInitialStates); //this is stupid
    }
}
