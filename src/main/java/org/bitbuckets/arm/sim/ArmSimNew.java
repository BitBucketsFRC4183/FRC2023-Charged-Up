package org.bitbuckets.arm.sim;

import edu.wpi.first.math.VecBuilder;
import edu.wpi.first.math.Vector;
import edu.wpi.first.math.numbers.N2;
import edu.wpi.first.math.numbers.N6;
import edu.wpi.first.wpilibj.smartdashboard.MechanismLigament2d;
import org.bitbuckets.arm.ArmFeedFordward;
import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.hardware.IMotorController;
import org.bitbuckets.lib.hardware.MotorConfig;
import org.bitbuckets.lib.log.Debuggable;

public class ArmSimNew {

    final MechanismLigament2d upperLigament;
    final MechanismLigament2d lowerLigament;
    final MotorConfig upperConfig;
    final MotorConfig lowerConfig;
    final ArmFeedFordward armFeedFordward;

    final Debuggable debuggable;


    public ArmSimNew(MechanismLigament2d upperLigament, MechanismLigament2d lowerLigament, MotorConfig upperConfig, MotorConfig lowerConfig, ArmFeedFordward armFeedFordward, Debuggable debuggable) {
        this.upperLigament = upperLigament;
        this.lowerLigament = lowerLigament;
        this.upperConfig = upperConfig;
        this.lowerConfig = lowerConfig;
        this.armFeedFordward = armFeedFordward;
        this.debuggable = debuggable;
    }

    public ISetup<IMotorController> getLowerArmSetup() {
        return (p) -> {
            var l = new SimArmControllerPhysics(false, lowerConfig, this, lowerLigament);
            p.registerLogicLoop(l::setTheAngleOfTheSimShit);
            return l;
        } ;
    }

    public void logLoop () {
        debuggable.log("state-position0", shoulderElbowStates.get(0, 0));
        debuggable.log("state-velo0",shoulderElbowStates.get(1,0));
        debuggable.log("state-acc0",shoulderElbowStates.get(2,0));
        debuggable.log("state-position1", shoulderElbowStates.get(3, 0));
        debuggable.log("state-velo1", shoulderElbowStates.get(4, 0));
        debuggable.log("state-acc1", shoulderElbowStates.get(5, 0));

        debuggable.log("volt-setpoint0", voltageSetpoints.get(0, 0));
        debuggable.log("volt-setpoint1", voltageSetpoints.get(1, 0));
    }


    public ISetup<IMotorController> getUpperArmSetup() {

        return (p) ->{
            var l = new SimArmControllerPhysics(true, upperConfig, this, upperLigament);
            p.registerLogicLoop(l::setTheAngleOfTheSimShit);
            return l;
        };
    }

    Vector<N6> shoulderElbowStates = VecBuilder.fill(Math.PI / 2.0, Math.PI, 0.0, 0.0, 0.0, 0.0);
    Vector<N2> voltageSetpoints = VecBuilder.fill(0,0);

    public Vector<N2> readArmAccelerations() {
        return VecBuilder.fill(shoulderElbowStates.get(4,0), shoulderElbowStates.get(5,0));

    }

    public Vector<N2> readArmPositions() {
        return VecBuilder.fill(shoulderElbowStates.get(0,0), shoulderElbowStates.get(1,0));

    }

    public Vector<N2> readArmVelocities() {
        return VecBuilder.fill(shoulderElbowStates.get(2,0), shoulderElbowStates.get(3,0));
    }

    public Vector<N2> readLowerArmStates() {
        return VecBuilder.fill(shoulderElbowStates.get(0,0), shoulderElbowStates.get(2,0));

    }

    public Vector<N2> readUpperArmStates() {
        return VecBuilder.fill(shoulderElbowStates.get(1,0), shoulderElbowStates.get(3,0));
    }

    public void setDesiredLowVoltage(double desiredLowVoltage) {
        voltageSetpoints.set(0, 0, desiredLowVoltage);
    }

    public void setDesiredUpperVoltage(double desiredUpperVoltage) {
        voltageSetpoints.set(1, 0, desiredUpperVoltage);
    }

    public void updateLoopDeltaTwenty() {
        shoulderElbowStates = armFeedFordward.simulate(VecBuilder.fill(shoulderElbowStates.get(0,0), shoulderElbowStates.get(1,0), shoulderElbowStates.get(2,0), shoulderElbowStates.get(3,0)), voltageSetpoints, 0.02);
    }
}


