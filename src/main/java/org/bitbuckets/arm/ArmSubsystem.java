package org.bitbuckets.arm;

import org.bitbuckets.auto.AutoSubsystem;
import org.bitbuckets.lib.log.ILoggable;

public class ArmSubsystem {

    //make motors


    final ArmInput armInput;
    final ArmControl armControl;
    final ILoggable<String> mode;

    ArmFSM state = ArmFSM.MANUAL;
    ArmFSM nextState = ArmFSM.MANUAL;
    public String logState = "MANUAL";

    public ArmSubsystem(ArmInput armInput, ArmControl armControl, ILoggable<String> mode) {
        this.armInput = armInput;
        this.armControl = armControl;
        this.mode = mode;
    }



    //private double CONTROL_JOINT_OUTPUT = 0.1;

    //calculated gearRatio
    //private double gearRatio = (5 * 4 * 3) / (12. / 30.);


    public void teleopPeriodic() {

        if (armInput.isCalibratedPressed()) {
            armControl.calibrateLowerArm();
            armControl.calibrateUpperArm();
            System.out.println("Arms calibrated!");
        }
        if (armInput.isDisablePositionControlPressed()) {
            state = ArmFSM.MANUAL;
            logState = "MANUAL";
        }

        switch (state) {
            case MANUAL:
                logState = "MANUAL";
                armControl.manuallyMoveLowerArm(armInput.getLowerArm_PercentOutput());
                armControl.manuallyMoveUpperArm(armInput.getUpperArm_PercentOutput());
                if (armInput.isStoragePressed()) {
                    state = ArmFSM.STORAGE;
                } else if (armInput.isHumanIntakePressed()) {
                    state = ArmFSM.PREPARE;
                    nextState = ArmFSM.HUMAN_INTAKE;
                } else if (armInput.isScoreMidPressed()) {
                    state = ArmFSM.PREPARE;
                    nextState = ArmFSM.SCORE_MID;
                } else if (armInput.isScoreHighPressed()) {
                    state = ArmFSM.PREPARE;
                    nextState = ArmFSM.SCORE_HIGH;
                }
                break;
            case STORAGE:
                if (armControl.storeArm()) {
                    logState = "STORAGE";
                    state = ArmFSM.MANUAL;
                }
                break;
            case PREPARE:
                if (armControl.prepareArm()){
                    logState = "PREPARE";
                    state = nextState;
                }
                break;
            case HUMAN_INTAKE:
                if (armControl.humanIntake()) {
                    logState = "HUMAN_INTAKE";
                    state = ArmFSM.MANUAL;
                }
                break;
            case SCORE_MID:
                if (armControl.scoreMid()) {
                    logState = "SCORE_MID";
                    state = ArmFSM.MANUAL;
                }
                break;
            case SCORE_HIGH:
                if(armControl.scoreHigh()){
                    logState = "SCORE_HIGH";
                    state = ArmFSM.MANUAL;
                }
                break;
        }
        mode.log(getLogState());
    }

    public String getLogState() {
        return logState;
    }




}




        /*
        if (armInput.isCalibratedPressed()) {
            armControl.calibrateLowerArm();
            armControl.calibrateUpperArm();
            System.out.println("Arms calibrated!");
        }

        switch (state)
        {
            case MANUAL:
                if (armInput.isStoragePressed()){
                    state = ArmFSM.STORAGE;
                }
                else if (armInput.isHumanIntakePressed()){
                    state = ArmFSM.PREPARE;
                    nextState = ArmFSM.HUMAN_INTAKE;
                }
                else if (armInput.isScoreMidPressed()){
                    state = ArmFSM.PREPARE;
                    nextState = ArmFSM.SCORE_MID;
                }
                else if (armInput.isScoreHighPressed()){
                    state = ArmFSM.PREPARE;
                    nextState = ArmFSM.SCORE_HIGH;
                }
                break;
            case POSITION_CONTROL:
                if (armInput.isDisablePositionControlPressed()) {
                    state = ArmFSM.MANUAL;
                }
                else{
                    switch (state){
                        case STORAGE:
                            armControl.storeArm();
                            break;
                        case PREPARE:
                            armControl.prepareArm();
                            state = nextState;
                        case HUMAN_INTAKE:
                            armControl.intakeHumanPlayer();
                            break;
                        case SCORE_MID:
                            armControl.scoreMid();
                            break;
                        case SCORE_HIGH:
                            armControl.scoreHigh();
                            break;
                        }
                    state = ArmFSM.MANUAL;

                }

                }
        }
        */

        /*

        switch (state) {
            case MANUAL:
                if (armInput.isIntakeHumanPressed()) {
                    state = ArmFSM.POSITION_CONTROL;
                    positionMode = "IntakeHuman";
                } else if (armInput.isIntakeGroundPressed()) {
                    state = ArmFSM.POSITION_CONTROL;
                    positionMode = "IntakeGround";
                } else if (armInput.isScoreMidPressed()) {
                    state = ArmFSM.POSITION_CONTROL;
                    positionMode = "ScoreMid";
                } else if (armInput.isScoreHighPressed()) {
                    state = ArmFSM.POSITION_CONTROL;
                    positionMode = "ScoreHigh";
                } else {
                    armControl.manuallyMoveLowerArm(armInput.getLowerArm_PercentOutput());
                    armControl.manuallyMoveUpperArm(armInput.getUpperArm_PercentOutput());
                }
            case PREPARE:
                armControl.
                break;
            case POSITION_CONTROL:
                if (armInput.isDisablePositionControlPressed()) {
                    state = ArmFSM.MANUAL;
                } else {
                    switch (positionMode) {
                        case "IntakeHuman":
                            armControl.intakeHumanPlayer();
                            break;
                        case "IntakeGround":
                            armControl.intakeGround();
                            break;
                        case "ScoreMid":
                            armControl.scoreMid();
                            break;
                        case "ScoreHigh":
                            armControl.scoreHigh();
                            break;
                    }
                }
                break;
        }


        mode.log(positionMode);

    }

         */

