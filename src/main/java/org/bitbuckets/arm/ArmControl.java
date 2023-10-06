package org.bitbuckets.arm;

import config.Arm;
import edu.wpi.first.hal.HALUtil;
import edu.wpi.first.hal.simulation.RoboRioDataJNI;
import edu.wpi.first.math.VecBuilder;
import edu.wpi.first.math.Vector;
import edu.wpi.first.math.numbers.N2;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj2.command.Subsystem;
import org.bitbuckets.lib.control.IPIDCalculator;
import org.bitbuckets.lib.core.HasLifecycle;
import org.bitbuckets.lib.core.HasLogLoop;
import org.bitbuckets.lib.hardware.IAbsoluteEncoder;
import org.bitbuckets.lib.hardware.IMotorController;
import org.bitbuckets.lib.log.IDebuggable;

public class ArmControl implements HasLogLoop, HasLifecycle, Subsystem {

    public static final ArmComponent COMPONENT = (ArmComponent) new Object();

    //needs a 1 by 3 mat describing correctness
    final ArmDynamics ff;

    final IMotorController lowerArm;
    final IMotorController upperArm;
    final IMotorController gripperWheelMotor;
    final IMotorController gripperClawMotor;

    final IPIDCalculator lowerArmControl_rotations;
    final IPIDCalculator upperArmControl_rotations;

    final IAbsoluteEncoder clawAbsEncoder;
    final IDebuggable debuggable;


    public ArmControl(ArmDynamics ff, IMotorController lowerArm, IMotorController upperArm, IPIDCalculator lowerArmControl_rotations, IPIDCalculator upperArmControl_rotations, IMotorController gripperActuator, IMotorController gripperClawMotor, IAbsoluteEncoder clawAbsEncoder, IDebuggable debuggable) {
        this.ff = ff;
        this.lowerArm = lowerArm;
        this.upperArm = upperArm;
        this.lowerArmControl_rotations = lowerArmControl_rotations;
        this.upperArmControl_rotations = upperArmControl_rotations;
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

        var ffVoltageVector = ff.feedforward(
                VecBuilder.fill(
                        lowerArm_rot * Math.PI * 2.0,
                        upperArm_rot * Math.PI * 2.0
                )
        );


        var lowerArmFFVoltage = ffVoltageVector.get(0, 0);
        var lowerArmFeedbackVoltage = lowerArmControl_rotations.calculateNext(
                lowerArm.getMechanismPositionAccum_rot(),
                lowerArm_rot
        );

        var upperArmFFVoltage = ffVoltageVector.get(1, 0);
        var upperArmFeedbackVoltage = upperArmControl_rotations.calculateNext(
                upperArm.getMechanismPositionAccum_rot(),
                upperArm_rot
        );

        lowerArm.moveAtVoltage(lowerArmFFVoltage + lowerArmFeedbackVoltage);
        upperArm.moveAtVoltage(upperArmFFVoltage + upperArmFeedbackVoltage);

    }

    public void commandGripperClawToPosition(double gripperMotorPosition_rotations) {
        gripperClawMotor.moveToPosition_mechanismRotations(gripperMotorPosition_rotations);
    }

    public double gripperClawError_rotations() {
        return gripperClawMotor.getError_mechanismRotations();
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
            gripperWheelMotor.moveAtPercent(-0.2);
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
        gripperClawMotor.moveToPosition_mechanismRotations(0.35);
       // gripperWheelMotor.moveAtPercent(0.8);
  //     gripperClawMotor.moveAtPercent(-0.5);
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


    public void commandArmToPercent(double lowerArmPercent, double upperArmPercent) {
        lowerArm.moveAtPercent(lowerArmPercent);
        upperArm.moveAtPercent(upperArmPercent);
    }

    public void commandArmToVoltage(double lowerArmVoltage, double upperArmVoltage) {
        lowerArm.moveAtVoltage(lowerArmVoltage);
        upperArm.moveAtVoltage(upperArmVoltage);
    }

    public void zero() {
        lowerArm.forceOffset_mechanismRotations(0);
    }

    /**
     * @return error vector based on the pid controllers. wrapped rotations that can be -inf to inf
     */
    public Vector<N2> getErrorQuantity_wrappedRotations() {
        return VecBuilder.fill(
                lowerArmControl_rotations.lastError(),
                upperArmControl_rotations.lastError()
        );
    }

    @Override
    public void logLoop() {
        debuggable.log("abs-angle-upper-arm", upperArm.getAbsoluteEncoder_rotations());
        debuggable.log("claw-abs-encoder-pos",clawAbsEncoder.getAbsoluteAngle());
    }



}
