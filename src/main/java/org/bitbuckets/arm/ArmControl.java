package org.bitbuckets.arm;

import config.Arm;
import edu.wpi.first.math.VecBuilder;
import org.bitbuckets.lib.control.IPIDCalculator;
import org.bitbuckets.lib.core.HasLogLoop;
import org.bitbuckets.lib.hardware.IMotorController;
import org.bitbuckets.lib.log.IDebuggable;

public class ArmControl implements HasLogLoop {

    //needs a 1 by 3 mat describing correctness
    final ArmDynamics ff;

    final IMotorController lowerArm;
    final IMotorController upperArm;

    //these must be continuous and bound (i.e. wrap from 2pi to 0)
    final IPIDCalculator lowerArmControl;
    final IPIDCalculator upperArmControl;
    final IMotorController gripperWheelMotor;
    final IMotorController gripperClawMotor;

    final IDebuggable debuggable;


    public ArmControl(ArmDynamics ff, IMotorController lowerArm, IMotorController upperArm, IPIDCalculator lowerArmControl, IPIDCalculator upperArmControl, IMotorController gripperActuator, IMotorController gripperClawMotor, IDebuggable debuggable) {
        this.ff = ff;
        this.lowerArm = lowerArm;
        this.upperArm = upperArm;
        this.lowerArmControl = lowerArmControl;
        this.upperArmControl = upperArmControl;
        this.gripperWheelMotor = gripperActuator;
        this.gripperClawMotor = gripperClawMotor;
        this.debuggable = debuggable;
    }

    /**
     * Commands the gripper to certain places, independent of what the limb is doing right now
     *
     * @param lowerArm_rot wrt zero as all the way out to the right
     * @param upperArm_rot wrt zero as all the way out to the right if lower arm is all the way out to the right
     */
    public void commandArmToState(double lowerArm_rot, double upperArm_rot) {

        var ffVoltageVector = ff.feedforward(VecBuilder.fill(lowerArm_rot * Math.PI * 2.0, upperArm_rot * Math.PI * 2.0));


        var lowerArmFFVoltage = ffVoltageVector.get(0, 0);
        var lowerArmFeedbackVoltage = lowerArmControl.calculateNext(
                lowerArm.getMechanismPositionAccum_rot(),
                lowerArm_rot
        );

        var upperArmFFVoltage = ffVoltageVector.get(1, 0);
        var upperArmFeedbackVoltage = upperArmControl.calculateNext(
                upperArm.getMechanismPositionAccum_rot(),
                upperArm_rot
        );

        lowerArm.moveAtVoltage(lowerArmFFVoltage + lowerArmFeedbackVoltage);
        upperArm.moveAtVoltage(upperArmFFVoltage + upperArmFeedbackVoltage);

    }

    public void doNothing() {
        lowerArm.moveAtVoltage(0);
        upperArm.moveAtVoltage(0);
    }


    public void zeroArmAbs() {
        double absAngleRot = upperArm.getAbsoluteEncoder_rotations() - Arm.UPPER_ARM_OFFSET;

        upperArm.forceOffset_mechanismRotations(absAngleRot);

    }

    public void gripperLoop() {
        gripperWheelMotor.moveAtPercent(-0.2);
    }

    public double getUpperAbsEncoderAngle() {
        return upperArm.getAbsoluteEncoder_rotations();
    }


    public void outtakeGripper() {
        gripperWheelMotor.moveAtPercent(0.5);
    }

    public void intakeGripperCone() {
        gripperWheelMotor.moveAtPercent(-1.0);
        gripperClawMotor.moveToPosition_mechanismRotations(-0.5);
    }

    public void intakeGripperCube() {
        gripperWheelMotor.moveAtPercent(-1.0);
        gripperClawMotor.moveToPosition_mechanismRotations(0);
    }

    public void stopGripper() {
        gripperWheelMotor.moveAtPercent(0);
    }


    public void stopTheArm() {
        lowerArm.moveAtVoltage(0);
        upperArm.moveAtVoltage(0);

    }

    public void commandArmToPercent(double lowerArmPercent, double upperArmPercent) {
        lowerArm.moveAtPercent(lowerArmPercent);
        upperArm.moveAtPercent(upperArmPercent);

    }

    public void zero() {
        lowerArm.forceOffset_mechanismRotations(0);
    }


    public double getErrorQuantity() {
        return 1;
    }

    @Override
    public void logLoop() {
        debuggable.log("abs-angle", upperArm.getAbsoluteEncoder_rotations());
    }

}
