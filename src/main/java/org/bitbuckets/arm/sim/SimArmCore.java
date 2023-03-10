package org.bitbuckets.arm.sim;

import edu.wpi.first.math.VecBuilder;
import edu.wpi.first.math.Vector;
import edu.wpi.first.math.numbers.N2;
import edu.wpi.first.math.numbers.N4;
import org.bitbuckets.arm.ArmDynamics;
import org.bitbuckets.lib.core.HasLoop;

public class SimArmCore implements HasLoop {

    final ArmDynamics dynamics;

    public SimArmCore(ArmDynamics dynamics, Vector<N4> shoulderElbowStates) {
        this.shoulderElbowStates = shoulderElbowStates;
        this.dynamics = dynamics;
    }

    //initial states
    Vector<N4> shoulderElbowStates;
    Vector<N2> voltageSetpoints = VecBuilder.fill(0,0);

    double getArmPosition(SimJoint joint) {
        if (joint == SimJoint.SHOULDER) {
            return shoulderElbowStates.get(0,0);
        } else {
            return shoulderElbowStates.get(1,0);
        }
    }

    double getArmVelocity(SimJoint joint) {
        if (joint == SimJoint.SHOULDER) {
            return shoulderElbowStates.get(2,0);
        } else {
            return shoulderElbowStates.get(3,0);
        }
    }

    void setVoltage(SimJoint joint, double voltage) {
        if (joint == SimJoint.SHOULDER) {
            voltageSetpoints.set(0, 0, voltage);
        } else {
            voltageSetpoints.set(1, 0, voltage);
        }
    }


    @Override
    public void loop() {
        shoulderElbowStates = dynamics.simulate(
                VecBuilder.fill(
                        shoulderElbowStates.get(0,0),
                        shoulderElbowStates.get(1,0),
                        shoulderElbowStates.get(2,0),
                        shoulderElbowStates.get(3,0)
                ),
                voltageSetpoints,
                0.02
        );
    }
}