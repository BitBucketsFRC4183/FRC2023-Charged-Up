package org.bitbuckets.arm.sim;

import edu.wpi.first.math.VecBuilder;
import edu.wpi.first.math.Vector;
import edu.wpi.first.math.numbers.N2;
import edu.wpi.first.math.numbers.N4;
import org.bitbuckets.arm.ArmDynamics;
import org.bitbuckets.lib.core.HasLoop;
import org.bitbuckets.lib.debug.IDebuggable;

public class SimArmCore implements HasLoop {

    final ArmDynamics dynamics;
    final IDebuggable debuggable;

    public SimArmCore(ArmDynamics dynamics, IDebuggable debuggable, Vector<N4> shoulderElbowStates) {
        this.debuggable = debuggable;
        this.shoulderElbowStates = shoulderElbowStates;
        this.dynamics = dynamics;
    }

    //initial states
    Vector<N4> shoulderElbowStates;
    Vector<N2> voltageSetpoints = VecBuilder.fill(0,0);



    double getArmPosition_rotations(SimJoint joint) {
        if (joint == SimJoint.SHOULDER) {
            return shoulderElbowStates.get(0,0) / Math.PI / 2.0;
        } else {
            return shoulderElbowStates.get(1,0) / Math.PI / 2.0;
        }
    }


    double getArmVelocity_rotationsPerSecond(SimJoint joint) {
        if (joint == SimJoint.SHOULDER) {
            return shoulderElbowStates.get(2,0) / Math.PI / 2.0;
        } else {
            return shoulderElbowStates.get(3,0) / Math.PI / 2.0;
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

        debuggable.log("voltage", voltageSetpoints.toString());
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