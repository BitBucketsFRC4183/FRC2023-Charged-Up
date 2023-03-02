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
            armControl.zeroToStartingPosition(); //Assume it's at the starting position lmao
        }
        if (operatorInput.isUserInputZeroed()) {
            armControl.zero(); //assume where we are is zero. Only do this if you really have to since zeroing needs
            //to go outside frame perimeter, and you can only do that in a match L
        }

        System.out.println(shouldDoNext);

        //handle inputs, which will calculate what the next input of the robot is
        handleInputs();
        handleLogic();

    }


    //generates what the FSM should do. Will modify shouldDoNext if something has happened
    void handleInputs() {
        if (operatorInput.isStopPidPressed() && autoSubsystem.state() != AutoFSM.AUTO_RUN) {
            shouldDoNext = ArmFSM.IDLE;
        }

        if (autoSubsystem.state() == AutoFSM.DISABLED) {
            shouldDoNext = ArmFSM.IDLE;
        }

        if (autoSubsystem.state() == AutoFSM.AUTO_RUN) {
            if (autoSubsystem.sampleHasEventStarted("arm-storage")) {
                shouldDoNext = ArmFSM.STORAGE;
            }
            if (autoSubsystem.sampleHasEventStarted("arm-prepare")) {
                shouldDoNext = ArmFSM.PREPARE;
            }
            if (autoSubsystem.sampleHasEventStarted("arm-score-high")) {
                shouldDoNext = ArmFSM.SCORE_HIGH;
            }
            if (autoSubsystem.sampleHasEventStarted("arm-human-intake")) {
                shouldDoNext = ArmFSM.HUMAN_INTAKE;
            }
            if (autoSubsystem.sampleHasEventStarted("arm-ground-intake")) {
                shouldDoNext = ArmFSM.GROUND_INTAKE;
            }
        }

        if (autoSubsystem.state() == AutoFSM.TELEOP) {

            if (operatorInput.isHumanIntakePressed()) {
                shouldDoNext = ArmFSM.HUMAN_INTAKE;
            }
            //TODO ground intake button
            if (operatorInput.isStoragePressed()) {
                shouldDoNext = ArmFSM.STORAGE;
            }
            if (operatorInput.isScoreHighPressed()) {
                shouldDoNext = ArmFSM.SCORE_HIGH;
            }
            if (operatorInput.isScoreMidPressed()) {
                shouldDoNext = ArmFSM.SCORE_MID;
            }
            if (operatorInput.isScoreLowPressed()) {
                shouldDoNext = ArmFSM.SCORE_LOW;
            }
            if (operatorInput.isDebugDegreesPressed()) {
                shouldDoNext = ArmFSM.DEBUG_TO_DEGREES;
            }

            if (operatorInput.isManualModePressed()) {
                shouldDoNext = ArmFSM.MANUAL;
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

        //TODO fill out the rest
    }
}
