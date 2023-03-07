package org.bitbuckets.arm;

import config.Arm;
import edu.wpi.first.math.VecBuilder;
import org.bitbuckets.lib.control.IPIDCalculator;
import org.bitbuckets.lib.hardware.IMotorController;

public class ArmControl {

    //needs a 1 by 3 mat describing correctness
    final ArmDynamics ff;

    final IMotorController lowerArm;
    final IMotorController upperArm;

    //these must be continuous and bound (i.e. wrap from 2pi to 0)
    final IPIDCalculator lowerArmControl;
    final IPIDCalculator upperArmControl;
    final IMotorController gripperActuator;


    public ArmControl(ArmDynamics ff, IMotorController lowerArm, IMotorController upperArm, IPIDCalculator lowerArmControl, IPIDCalculator upperArmControl, IMotorController gripperActuator) {
        this.ff = ff;
        this.lowerArm = lowerArm;
        this.upperArm = upperArm;
        this.lowerArmControl = lowerArmControl;
        this.upperArmControl = upperArmControl;
        this.gripperActuator = gripperActuator;
    }

    /**
     * Commands the gripper to certain places, independent of what the limb is doing right now
     *
     * @param lowerArm_rot      wrt zero as all the way out to the right
     * @param upperArm_rot      wrt zero as all the way out to the right if lower arm is all the way out to the right
     * @param gripperShouldOpen
     */
    public void commandArmToState(double lowerArm_rot, double upperArm_rot, boolean gripperShouldOpen) {

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

//
//        System.out.println("FF LOW: " + lowerArmFFVoltage);
//        System.out.println("FB LOW: " + lowerArmFeedbackVoltage);
//
//        System.out.println("FF UP: " + upperArmFFVoltage);
//        System.out.println("FB UP: " + upperArmFeedbackVoltage);

        lowerArm.moveAtVoltage(lowerArmFFVoltage + lowerArmFeedbackVoltage);
        upperArm.moveAtVoltage(upperArmFFVoltage + upperArmFeedbackVoltage);

        if (gripperShouldOpen) {
            gripperActuator.moveToPosition_mechanismRotations(Arm.GRIPPER_SETPOINT_MOTOR_ROTATIONS);
        } else {
            stopGripper();
        }

    }

    public void gripperResetonLimit() {
        if (gripperActuator.isForwardLimitSwitchPressed()) {
            gripperActuator.forceOffset_mechanismRotations(0);
        }
    }

    public void zeroArmAbs() {
        double absAngleRot = upperArm.getAbsoluteEncoder_rotations() - Arm.UPPER_ARM_OFFSET;

        upperArm.forceOffset_mechanismRotations(-absAngleRot);

    }

    public double getUpperAbsEncoderAngle() {
        return upperArm.getAbsoluteEncoder_rotations();
    }


    public void openGripper() {
        gripperActuator.moveAtPercent(0.6);

        //     }
    }


    public void closeGripper() {
        gripperActuator.moveAtPercent(-0.6);

    }

    public void stopGripper() {
        gripperActuator.moveAtPercent(0);
    }


    public void commandArmToPercent(double lowerArmPercent, double upperArmPercent, boolean gripperShouldOpen) {
        lowerArm.moveAtPercent(lowerArmPercent);
        upperArm.moveAtPercent(upperArmPercent);

        if (gripperShouldOpen) {
            //gripperActuator.moveToPosition_mechanismRotations(Arm.GRIPPER_SETPOINT_MOTOR_ROTATIONS);
        } else {
            //gripperActuator.goLimp(); //let the ropes pull it back
        }
    }

    public void zero() {
        lowerArm.forceOffset_mechanismRotations(0);
        upperArm.forceOffset_mechanismRotations(0);
    }


    public double getErrorQuantity() {
        return 1;
    }

    //probably doesn't work but I need something to test right now
    public boolean isErrorSmallEnough(double delta) {
        return Math.abs(lowerArm.getError_mechanismRotations()) < delta && Math.abs(upperArm.getError_mechanismRotations()) < delta;
    }
}
