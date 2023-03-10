package org.bitbuckets.arm;

import config.Arm;
import org.bitbuckets.OperatorInput;
import org.bitbuckets.auto.AutoFSM;
import org.bitbuckets.auto.AutoSubsystem;
import org.bitbuckets.lib.core.HasLoop;
import org.bitbuckets.lib.debug.IDebuggable;

public class ArmSubsystem implements HasLoop {

    final OperatorInput operatorInput;
    final ArmControl armControl;
    final AutoSubsystem autoSubsystem;
    final IDebuggable debuggable;


    public ArmSubsystem(OperatorInput operatorInput, ArmControl armControl, AutoSubsystem autoSubsystem, IDebuggable debuggable) {
        this.operatorInput = operatorInput;
        this.armControl = armControl;
        this.autoSubsystem = autoSubsystem;
        this.debuggable = debuggable;
    }

    ArmFSM shouldDoNext = ArmFSM.IDLE;
    AutoFSM autoStates = AutoFSM.INITIALIZATION;

    @Override
    public void loop() {
        //handle arm calibration
        armControl.gripperResetonLimit();

        if (operatorInput.isZeroArmPressed()) {

            armControl.zero(); //assume where we are is zero. Only do this if you really have to since zeroing needs
            //to go outside frame perimeter, and you can only do that in a match L
        }


        //handle inputs, which will calculate what the next input of the robot is
        handleStateTransitions();
        handleLogic();

        debuggable.log("state", shouldDoNext);
    }


    //generates what the FSM should do. Will modify shouldDoNext if something has happened
    void handleStateTransitions() {
        if (operatorInput.isStopPidPressed() && autoSubsystem.state() != AutoFSM.AUTO_RUN) {
            shouldDoNext = ArmFSM.IDLE;
            return;
        }

        if (autoSubsystem.state() == AutoFSM.DISABLED) {
            shouldDoNext = ArmFSM.IDLE;
            return;
        }

        if (autoSubsystem.state() == AutoFSM.AUTO_RUN) {
            if (autoSubsystem.sampleHasEventStarted("arm-storage")) {
                shouldDoNext = ArmFSM.STORAGE;
                return;
            }


            //Only scoring high when moving arm in auto

            if (autoSubsystem.sampleHasEventStarted("arm-human-intake")) {
                shouldDoNext = ArmFSM.HUMAN_INTAKE;
                return;
            }
            if (autoSubsystem.sampleHasEventStarted("arm-ground-intake")) {
                shouldDoNext = ArmFSM.GROUND_INTAKE;
                return;
            }

            //TODO legacy path event
            if (autoSubsystem.sampleHasEventStarted("collect")) {
                shouldDoNext = ArmFSM.STORAGE;
                return;
            }

            if (autoSubsystem.sampleHasEventStarted("gripper-open")) {
                shouldDoNext = ArmFSM.ACTUATE_GRIPPER;
                return;
            }

            if (autoSubsystem.sampleHasEventStarted("arm-scoreHigh")) {
                shouldDoNext = ArmFSM.SCORE_HIGH;
                return;
            }

            if (autoSubsystem.sampleHasEventStarted("arm-prepare")) {
                shouldDoNext = ArmFSM.PREPARE;
                return;
            }

            if (autoSubsystem.sampleHasEventStarted("arm-unstow")) {
                shouldDoNext = ArmFSM.UNSTOW;
                return;
            }
        }

        if (autoSubsystem.state() == AutoFSM.TELEOP) {

            if (operatorInput.isHumanIntakePressed()) {
                shouldDoNext = ArmFSM.HUMAN_INTAKE;
                return;
            }
            //TODO ground intake button
            if (operatorInput.isStoragePressed()) {
                shouldDoNext = ArmFSM.STORAGE;
                return;
            }
            if (operatorInput.isScoreHighPressed()) {
                shouldDoNext = ArmFSM.SCORE_HIGH;
                return;
            }
            if (operatorInput.isScoreMidPressed()) {
                shouldDoNext = ArmFSM.SCORE_MID;
                return;
            }
            if (operatorInput.isScoreLowPressed()) {
                shouldDoNext = ArmFSM.SCORE_LOW;
                return;
            }
            if (operatorInput.isLoadPresed()) {
                shouldDoNext = ArmFSM.LOAD;
                return;
            }

            if (operatorInput.isManualModePressed()) {
                shouldDoNext = ArmFSM.MANUAL;
                return;
            }


        }
    }

    //acts on shouldDoNext and then updates it to the result state if it has managed to complete it's task
    void handleLogic() {


        if (operatorInput.openGripperPressed()) {
            armControl.openGripper();
        } else if (operatorInput.closeGripperPressed()) {
            armControl.closeGripper();
        } else if (!operatorInput.closeGripperPressed() && !operatorInput.openGripperPressed()) {
            armControl.stopGripper();
        }

        if (autoSubsystem.state() == AutoFSM.DISABLED) { //arm can move after auto fsm has ended, so that if we make a mistake it can still win without us

            return;
        }

        if (shouldDoNext == ArmFSM.MANUAL) {


            armControl.commandArmToPercent(
                    operatorInput.getLowerArm_PercentOutput() * 0.35,
                    operatorInput.getUpperArm_PercentOutput() * 0.35,
                    !operatorInput.closeGripperPressed()
            );
        }

        if (shouldDoNext == ArmFSM.STORAGE) {
            armControl.commandArmToState(
                    0.168,
                    -0.222,
                    !operatorInput.closeGripperPressed()
            );
        }
        if (shouldDoNext == ArmFSM.LOAD) {
            armControl.commandArmToState(
                    0.008,
                    -0.23,
                    true);
        }

        if (shouldDoNext == ArmFSM.ACTUATE_GRIPPER) {
            armControl.openGripper();
        }

        //TODO fix the numbers
        if (shouldDoNext == ArmFSM.DEBUG_TO_DEGREES) {
            armControl.commandArmToState(
                    0, 0,
                    !operatorInput.closeGripperPressed()
            );

            if (armControl.getErrorQuantity() > Arm.ARM_TOLERANCE_TO_MOVE_ON) {
                shouldDoNext = ArmFSM.IDLE;
            }
        }

        if (shouldDoNext == ArmFSM.SCORE_MID) {

            armControl.commandArmToState(0.008, -0.227, true);


        }
        if (shouldDoNext == ArmFSM.SCORE_HIGH) {
            armControl.commandArmToState(-0.126, 0.0, true);


        }
        if (shouldDoNext == ArmFSM.GROUND_INTAKE) {
            armControl.commandArmToState(0.581, -0.274, true);

        }

        if (shouldDoNext == ArmFSM.UNSTOW) {
            armControl.commandArmToState(- 0.1,armControl.upperArm.getMechanismPositionAccum_rot(),false);


        }
        //TODO fill out the rest
    }
}
