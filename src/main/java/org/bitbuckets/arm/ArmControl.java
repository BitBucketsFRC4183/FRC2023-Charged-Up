package org.bitbuckets.arm;

import config.Arm;
import edu.wpi.first.math.VecBuilder;
import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.wpilibj.DriverStation;
import org.bitbuckets.lib.control.IPIDCalculator;
import org.bitbuckets.lib.core.HasLifecycle;
import org.bitbuckets.lib.core.HasLogLoop;
import org.bitbuckets.lib.hardware.IAbsoluteEncoder;
import org.bitbuckets.lib.hardware.IMotorController;
import org.bitbuckets.lib.log.IDebuggable;

public class ArmControl implements HasLogLoop, HasLifecycle {

    //needs a 1 by 3 mat describing correctness
    final ArmDynamics ff;

    final IMotorController lowerArm;
    final IMotorController upperArm;

    //these must be continuous and bound (i.e. wrap from 2pi to 0)
    final IPIDCalculator lowerArmControl;
    final IPIDCalculator upperArmControl;
    final IMotorController gripperWheelMotor;
    final IMotorController gripperClawMotor;

    final IAbsoluteEncoder clawAbsEncoder;

    final IDebuggable debuggable;


    public ArmControl(ArmDynamics ff, IMotorController lowerArm, IMotorController upperArm, IPIDCalculator lowerArmControl, IPIDCalculator upperArmControl, IMotorController gripperActuator, IMotorController gripperClawMotor, IAbsoluteEncoder clawAbsEncoder, IDebuggable debuggable) {
        this.ff = ff;
        this.lowerArm = lowerArm;
        this.upperArm = upperArm;
        this.lowerArmControl = lowerArmControl;
        this.upperArmControl = upperArmControl;
        this.gripperWheelMotor = gripperActuator;
        this.gripperClawMotor = gripperClawMotor;
        this.clawAbsEncoder = clawAbsEncoder;
        this.debuggable = debuggable;
    }

    @Override
    public void autonomousInit() {
        //lowerArmControl.rawAccess(ProfiledPIDController.class).reset(lowerArm.getMechanismPositionAccum_rot());
        //upperArmControl.rawAccess(ProfiledPIDController.class).reset(upperArm.getMechanismPositionAccum_rot());
    }

    @Override
    public void teleopInit() {
        //lowerArmControl.rawAccess(ProfiledPIDController.class).reset(lowerArm.getMechanismPositionAccum_rot());
        //upperArmControl.rawAccess(ProfiledPIDController.class).reset(upperArm.getMechanismPositionAccum_rot());
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
        if(DriverStation.isAutonomous())
        {
               gripperWheelMotor.moveAtVoltage(0);
        }
    }


    public void zeroArmAbs() {
        double absAngleRot = upperArm.getAbsoluteEncoder_rotations() - Arm.UPPER_ARM_OFFSET;

        upperArm.forceOffset_mechanismRotations(absAngleRot);

    }

    public void zeroClawAbs()
    {
        double absAngleRot = clawAbsEncoder.getAbsoluteAngle();

        gripperClawMotor.forceOffset_mechanismRotations(absAngleRot);

    }

    int blitzToggle = 0;


    public void gripperHold() {

        if(blitzToggle == 0)
        {
            gripperWheelMotor.moveAtPercent(-0.2);
                gripperClawMotor.moveAtPercent(0.4);
        }

        else {
            gripperWheelMotor.moveAtPercent(-0);
            gripperClawMotor.moveAtPercent(-0);
        }
        blitzToggle += 1;
        if(blitzToggle > 1)
        {
            blitzToggle = 0;
        }

    }



    public double getUpperAbsEncoderAngle() {
        return upperArm.getAbsoluteEncoder_rotations();
    }


    public void openGripper() {
      //  gripperClawMotor.moveToPosition_mechanismRotations(0.35);
       // gripperWheelMotor.moveAtPercent(0.8);
       gripperClawMotor.moveAtPercent(-0.5);
    }

    public void intakeGripperCone() {
        gripperWheelMotor.moveAtPercent(-0.9);
        gripperClawMotor.moveToPosition_mechanismRotations(0.69);
    }

    public void intakeGripperCube() {
        gripperWheelMotor.moveAtPercent(-0.9);
        gripperClawMotor.moveToPosition_mechanismRotations(0.55);
    }

    public void stopGripper() {
        gripperWheelMotor.moveAtPercent(0);
          gripperClawMotor.moveAtPercent(0);
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
        debuggable.log("abs-angle-upper-arm", upperArm.getAbsoluteEncoder_rotations());
        debuggable.log("claw-abs-encoder-pos",clawAbsEncoder.getAbsoluteAngle());
    }



}
