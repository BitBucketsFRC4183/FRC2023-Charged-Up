package org.bitbuckets.arm;

import config.Arm;
import edu.wpi.first.math.util.Units;
import org.bitbuckets.OperatorInput;
import org.bitbuckets.auto.AutoFSM;
import org.bitbuckets.auto.AutoSubsystem;
import org.bitbuckets.lib.core.HasLoop;

public class ArmSubsystem implements HasLoop {

    final OperatorInput operatorInput;
    final ArmControl armControl;
    final AutoSubsystem autoSubsystem;

    public ArmSubsystem(OperatorInput operatorInput, ArmControl armControl, AutoSubsystem autoSubsystem) {
        this.operatorInput = operatorInput;
        this.armControl = armControl;
        this.autoSubsystem = autoSubsystem;
    }

    ArmFSM shouldDoNext = ArmFSM.IDLE;

    @Override
    public void loop() {
        //handle arm calibration
        if (autoSubsystem.hasChanged() && autoSubsystem.state() == AutoFSM.INITIALIZATION) {
            System.out.println("System zeroed to starting position");

            armControl.zeroToStartingPosition(); //Assume it's at the starting position lmao
        }
        if (operatorInput.isZeroArmPressed()) {
            System.out.println("System zeroed to user input");

            armControl.zero(); //assume where we are is zero. Only do this if you really have to since zeroing needs
            //to go outside frame perimeter, and you can only do that in a match L
        }


        //handle inputs, which will calculate what the next input of the robot is
        handleStateTransitions();
        handleLogic();

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
            if (autoSubsystem.sampleHasEventStarted("arm-prepare")) {
                shouldDoNext = ArmFSM.PREPARE;
                return;
            }
            if (autoSubsystem.sampleHasEventStarted("arm-score-high")) {
                shouldDoNext = ArmFSM.SCORE_HIGH;
                return;
            }
            if (autoSubsystem.sampleHasEventStarted("arm-human-intake")) {
                shouldDoNext = ArmFSM.HUMAN_INTAKE;
                return;
            }
            if (autoSubsystem.sampleHasEventStarted("arm-ground-intake")) {
                shouldDoNext = ArmFSM.GROUND_INTAKE;
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
            if (operatorInput.isDebugDegreesPressed()) {
                shouldDoNext = ArmFSM.DEBUG_TO_DEGREES;
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
        if (autoSubsystem.state() == AutoFSM.DISABLED) { //arm can move after auto fsm has ended, so that if we fuck up it can still win without us
            return;
        }

        if (shouldDoNext == ArmFSM.MANUAL) {

            System.out.println(operatorInput.getLowerArm_PercentOutput());

            armControl.commandArmToPercent(
                    operatorInput.getLowerArm_PercentOutput(),
                    operatorInput.getUpperArm_PercentOutput(),
                    !operatorInput.closeGripperPressed()
            );
        }

        if (shouldDoNext == ArmFSM.STORAGE) {
            armControl.commandArmToState(
                    0.225,
                    -0.246,
                    !operatorInput.closeGripperPressed()
            );
        }

        //TODO fix the numbers
        if (shouldDoNext == ArmFSM.DEBUG_TO_DEGREES) {
            armControl.commandArmToState(
                    Units.degreesToRotations(45),
                    Units.degreesToRotations(45),
                    !operatorInput.closeGripperPressed()
            );

            if (armControl.getErrorQuantity() > Arm.ARM_TOLERANCE_TO_MOVE_ON) {
                shouldDoNext = ArmFSM.IDLE;
            }
        }

        if (shouldDoNext == ArmFSM.SCORE_MID) {
            armControl.commandArmToState(0.225, -0.246,true);

            if (armControl.getErrorQuantity() > Arm.ARM_TOLERANCE_TO_MOVE_ON) {
                shouldDoNext = ArmFSM.IDLE;
            }
        }

        //TODO fill out the rest
    }
}
