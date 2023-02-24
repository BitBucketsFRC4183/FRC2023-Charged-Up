package org.bitbuckets.arm;

import org.bitbuckets.gripper.GripperControl;
import org.bitbuckets.gripper.GripperFSM;
import org.bitbuckets.gripper.GripperInput;
import org.bitbuckets.auto.AutoFSM;
import org.bitbuckets.auto.AutoSubsystem;
import org.bitbuckets.lib.log.Debuggable;

public class ArmSubsystem {

    //make motors

    final ArmInput armInput;
    final ArmControl armControl;

    final GripperControl gripperControl;
    final GripperInput gripperInput;
    final Debuggable debuggable;
    final AutoSubsystem autoSubsystem;

    ArmFSM state = ArmFSM.DEFAULT; // Placeholder, default state
    ArmFSM nextState = ArmFSM.DEFAULT;

    GripperFSM gripperState = GripperFSM.MANUAL;

    public ArmSubsystem(ArmInput armInput, ArmControl armControl, GripperControl gripperControl, GripperInput gripperInput, Debuggable debuggable, AutoSubsystem autoSubsystem) {
        this.armInput = armInput;
        this.armControl = armControl;
        this.gripperControl = gripperControl;
        this.gripperInput = gripperInput;
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
            gripperState = GripperFSM.AUTO;
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
        gripperState = GripperFSM.TELEOP;
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
                if (gripperInput.ifGripperPressed()) {
                    gripperControl.openGripper();
                } else {
                    gripperControl.closeGripper();
                }

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

                //if X is pressed in sim (on keyboard)
                if (armInput.isStopPidPressed()) {
                    state = ArmFSM.TELEOP;
                }
                armControl.storeArm();
                gripperControl.closeGripper();
                if (armControl.isErrorSmallEnough(.1)) {
                    state = ArmFSM.TELEOP;
                }
                break;

            case PREPARE:
                armControl.prepareArm();
                if (armControl.isErrorSmallEnough(.1) || armInput.isStopPidPressed()) {
                    state = nextState;
                    gripperControl.openGripper();
                }

                break;

            case HUMAN_INTAKE:
                armControl.humanIntake();
                if (armControl.isErrorSmallEnough(.1) || armInput.isStopPidPressed()) {
                    state = ArmFSM.TELEOP;
                    gripperControl.openGripper();
                }
                break;

            case SCORE_LOW:
                armControl.scoreLow();
                if (armControl.isErrorSmallEnough(0.1) || armInput.isStopPidPressed()) {
                    state = ArmFSM.TELEOP;
                    if(gripperState == GripperFSM.AUTO){
                        gripperControl.openGripper();
                    }
                }
                break;

            case SCORE_MID:
                armControl.scoreMid();
                if (armControl.isErrorSmallEnough(.1) || armInput.isStopPidPressed()) {
                    state = ArmFSM.TELEOP;
                    if(gripperState == GripperFSM.AUTO){
                        gripperControl.openGripper();
                    }
                }
                break;

            case SCORE_HIGH:
                armControl.scoreHigh();
                if (armControl.isErrorSmallEnough(.1) || armInput.isStopPidPressed()) {
                    state = ArmFSM.TELEOP;
                    if(gripperState == GripperFSM.AUTO){
                        gripperControl.openGripper();
                    }
                }
                break;
        }
        debuggable.log("state", state.toString());

    }


}


