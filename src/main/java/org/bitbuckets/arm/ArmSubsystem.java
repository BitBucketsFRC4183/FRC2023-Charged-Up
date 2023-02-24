package org.bitbuckets.arm;

import org.bitbuckets.auto.AutoFSM;
import org.bitbuckets.auto.AutoSubsystem;
import org.bitbuckets.lib.debug.IDebuggable;


public class ArmSubsystem {

    //make motors

    final ArmInput armInput;
    final ArmControl armControl;
    final IDebuggable debuggable;
    final AutoSubsystem autoSubsystem;

    ArmFSM state = ArmFSM.DEFAULT; // Placeholder, default state
    ArmFSM nextState = ArmFSM.DEFAULT;

    public ArmSubsystem(ArmInput armInput, ArmControl armControl, IDebuggable debuggable, AutoSubsystem autoSubsystem) {
        this.armInput = armInput;
        this.armControl = armControl;
        this.debuggable = debuggable;
        this.autoSubsystem = autoSubsystem;
    }


    //private double CONTROL_JOINT_OUTPUT = 0.1;

    //calculated gearRatio
    //private double gearRatio = (5 * 4 * 3) / (12. / 30.);


    public void runLoop() {
        switch (state) {
            case DEFAULT:
                if (autoSubsystem.state() == AutoFSM.AUTO_RUN) {
                    state = ArmFSM.AUTO_PATHFINDING;
                    break;
                }
                if (autoSubsystem.state() == AutoFSM.TELEOP) {
                    state = ArmFSM.TELEOP;
                    break;
                }
                break;

            case AUTO_PATHFINDING:

                if (autoSubsystem.state() == AutoFSM.TELEOP || autoSubsystem.state() == AutoFSM.AUTO_ENDED) {
                    state = ArmFSM.TELEOP;
                    break;
                }
                autoPeriodic();
                break;

            case TELEOP:
                if (autoSubsystem.state() == AutoFSM.AUTO_RUN) {
                    state = ArmFSM.AUTO_PATHFINDING;
                    break;
                }
                teleopPeriodic();
                break;

        }
        debuggable.log("state", state.toString());

    }

        public void autoPeriodic() {
            if (autoSubsystem.sampleHasEventStarted("go-to-storage")) {
                state = ArmFSM.STORAGE;
            }
            if (autoSubsystem.sampleHasEventStarted("go-to-prepare")) {
                state = ArmFSM.PREPARE;
            }
            if (autoSubsystem.sampleHasEventStarted("score-high")) {
                state = ArmFSM.SCORE_HIGH;
            }
            if (autoSubsystem.sampleHasEventStarted("go-to-human-intake")) {
                state = ArmFSM.HUMAN_INTAKE;
            }
            if (autoSubsystem.sampleHasEventStarted("pick-up-game-piece")) {
                state = ArmFSM.GROUND_INTAKE;
            }
            switch (state) {
                case STORAGE:
                    armControl.storeArm();
                    break;
                case PREPARE:
                    armControl.prepareArm();
                    break;
                case SCORE_HIGH:
                    armControl.scoreHigh();
                    break;
                case HUMAN_INTAKE:
                    armControl.humanIntake();
                    break;
                case GROUND_INTAKE:
                    armControl.intakeGround();
            }
            debuggable.log("state", state.toString());

        }



    public void teleopPeriodic() {
        if (armInput.isCalibratedPressed()) {
            armControl.calibrateLowerArm();
            armControl.calibrateUpperArm();
            System.out.println("Arms calibrated!");
        }
        if (armInput.isDisablePositionControlPressed()) {
            state = ArmFSM.TELEOP;
        }

        switch (state) {
            case TELEOP:
                armControl.manuallyMoveLowerArm(armInput.getLowerArm_PercentOutput());
                armControl.manuallyMoveUpperArm(armInput.getUpperArm_PercentOutput());

                debuggable.log("lower", armInput.getLowerArm_PercentOutput());

                debuggable.log("line 49", true);

                if (armInput.isStoragePressed()) {
                    state = ArmFSM.STORAGE;
                    break;
                } else if (armInput.isHumanIntakePressed()) {
                    state = ArmFSM.PREPARE;
                    nextState = ArmFSM.HUMAN_INTAKE;
                    break;
                } else if (armInput.isScoreMidPressed()) {
                    debuggable.log("line 55", true);

                    state = ArmFSM.PREPARE;
                    nextState = ArmFSM.SCORE_MID;
                    break;
                } else if (armInput.isScoreHighPressed()) {
                    state = ArmFSM.PREPARE;
                    nextState = ArmFSM.SCORE_HIGH;
                    break;
                } else if (armInput.isScoreLowPressed()) {
                    state = ArmFSM.PREPARE;
                    nextState = ArmFSM.SCORE_LOW;
                    break;
                } else if (armInput.isDebugDegreesPressed()) {
                    state = ArmFSM.DEBUG_TO_DEGREES;
                    break;
                }
                break;

            case DEBUG_TO_DEGREES:
                if (armInput.isStopPidPressed()) {
                    state = ArmFSM.TELEOP;
                    break;
                }

                armControl.moveToSetpointOnly(0.25, 0.12);


                break;


            //if C is pressed in sim (on keyboard)
            case STORAGE:

                //goes into this if statement if X is pressed in sim (on keyboard)
                /**
                 * ONLY USEFUL FOR SIM (hopefully lol)
                 * basically, when looking at the next if statement, the error before the arm exits the current state is some number
                 * however, for small errors, the sim never thinks the error is small enough, hence it never exits the state
                 * this function allows the user to press a button to tell the sim to go to manual
                 */
                if (armInput.isStopPidPressed()) {
                    state = ArmFSM.TELEOP;
                    break;
                }
                armControl.storeArm();
                if (armControl.isErrorSmallEnough(.1)) {
                    state = ArmFSM.TELEOP;
                    break;
                }
                break;

            case PREPARE:
                armControl.prepareArm();
                if (armControl.isErrorSmallEnough(.1) || armInput.isStopPidPressed()) {
                    state = nextState;
                    break;
                }

                break;

            case HUMAN_INTAKE:
                armControl.humanIntake();
                if (armControl.isErrorSmallEnough(.1) || armInput.isStopPidPressed()) {
                    state = ArmFSM.TELEOP;
                    break;
                }
                break;

            case SCORE_LOW:
                armControl.scoreLow();
                if (armControl.isErrorSmallEnough(0.1) || armInput.isStopPidPressed()) {
                    state = ArmFSM.TELEOP;
                    break;
                }
                break;

            case SCORE_MID:
                armControl.scoreMid();
                if (armControl.isErrorSmallEnough(.1) || armInput.isStopPidPressed()) {
                    state = ArmFSM.TELEOP;
                    break;
                }
                break;

            case SCORE_HIGH:
                armControl.scoreHigh();
                if (armControl.isErrorSmallEnough(.1) || armInput.isStopPidPressed()) {
                    state = ArmFSM.TELEOP;
                    break;
                }
                break;
        }
        debuggable.log("state", state.toString());


    }


}


