package org.bitbuckets.arm;

import org.bitbuckets.auto.AutoFSM;
import org.bitbuckets.auto.AutoSubsystem;
import org.bitbuckets.lib.log.Debuggable;

public class ArmSubsystem {

    final ArmInput armInput;
    final ArmControl armControl;
    final Debuggable debuggable;
    final AutoSubsystem autoSubsystem;


    // state holds the current state of the FSM that the arm is in, with the default state being manual
    ArmFSM state = ArmFSM.MANUAL;

    // nextState holds the next state that the arm should go to AFTER it has completed the current actions commanded by state
    // default of nextState is manual, but changes when operator presses button that causes the arm to independently move to a new position
    ArmFSM nextState = ArmFSM.MANUAL;

    public ArmSubsystem(ArmInput armInput, ArmControl armControl, Debuggable debuggable, AutoSubsystem autoSubsystem) {
        this.armInput = armInput;
        this.armControl = armControl;
        this.debuggable = debuggable;
        this.autoSubsystem = autoSubsystem;
    }

    // Holds the ArmFSM
    AutoFSM lastRobotState;

    public void teleopPeriodic() {

        // Checks if calibration button on operator controller is pressed to reset encoder position of all motors to 0
        if (autoSubsystem.state() == AutoFSM.AUTO_RUN && lastRobotState == AutoFSM.DISABLED) {
            armControl.robotIsOn();
        }

        if (armInput.isCalibratedPressed()) {
            armControl.calibrateLowerArm();
            armControl.calibrateUpperArm();
            System.out.println("Arms calibrated!");
        }

        // Switches the current state to manual mode if enable manual mode is pressed on operator controller
        if (armInput.isDisablePositionControlPressed()) {
            state = ArmFSM.MANUAL;
        }

        // Arm finite state machine that dictates which case of commands the arm should follow based on its state
        // the state changes the nextState
        switch (state) {
            //
            case MANUAL:

                armControl.manuallyMoveLowerArm(armInput.getLowerArm_PercentOutput());
                armControl.manuallyMoveUpperArm(armInput.getUpperArm_PercentOutput());

                debuggable.log("line 49", true);

                if (armInput.isStoragePressed()) {
                    state = ArmFSM.STORAGE;
                } else if (armInput.isHumanIntakePressed()) {
                    state = ArmFSM.PREPARE;
                    nextState = ArmFSM.HUMAN_INTAKE;
                } else if (armInput.isScoreMidPressed()) {
                    debuggable.log("line 55", true);

                    state = ArmFSM.PREPARE;
                    nextState = ArmFSM.SCORE_MID;
                } else if (armInput.isScoreHighPressed()) {
                    state = ArmFSM.PREPARE;
                    nextState = ArmFSM.SCORE_HIGH;
                } else if (armInput.isScoreLowPressed()) {
                    state = ArmFSM.PREPARE;
                    nextState = ArmFSM.SCORE_LOW;
                }
                break;

            //goes into this case if C is pressed in sim (on keyboard)
            // Storage is the case that commands the arm to go into storage position, or the position the arm is not actively scoring or intaking pieces
            // uses InverseKinematics to dictate the positions the arm should move to in order to achieve the storage coords
            case STORAGE:

                //goes into this if statement if X is pressed in sim (on keyboard)
                /**
                 * ONLY USEFUL FOR SIM (hopefully lol)
                 * basically, when looking at the next if statement, the error before the arm exits the current state is some number
                 * however, for small errors, the sim never thinks the error is small enough, hence it never exits the state
                 * this function allows the user to press a button to tell the sim to go to manual
                 */
                if (armInput.isStopPidPressed()) {
                    state = ArmFSM.MANUAL;
                }
                armControl.storeArm();
                if (armControl.isErrorSmallEnough(.1)) {
                    state = ArmFSM.MANUAL;
                }
                break;

            // Prepare is the case that commands the arm to go backwards to avoid any obstacles when changing between any scoring mode and storage and vice versa
            case PREPARE:
                armControl.prepareArm();
                if (armControl.isErrorSmallEnough(.1) || armInput.isStopPidPressed()) {
                    state = nextState;
                }

                break;

            case HUMAN_INTAKE:
                armControl.humanIntake();
                if (armControl.isErrorSmallEnough(.1) || armInput.isStopPidPressed()) {
                    state = ArmFSM.MANUAL;
                }
                break;

            case SCORE_LOW:
                armControl.scoreLow();
                if (armControl.isErrorSmallEnough(0.1) || armInput.isStopPidPressed()) {
                    state = ArmFSM.MANUAL;
                }
                break;

            case SCORE_MID:
                armControl.scoreMid();
                if (armControl.isErrorSmallEnough(.1) || armInput.isStopPidPressed()) {
                    state = ArmFSM.MANUAL;
                }
                break;

            case SCORE_HIGH:
                armControl.scoreHigh();
                if (armControl.isErrorSmallEnough(.1) || armInput.isStopPidPressed()) {
                    state = ArmFSM.MANUAL;
                }
                break;
        }
        debuggable.log("state", state);
    }


}
