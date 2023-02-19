package org.bitbuckets.arm;

import org.bitbuckets.auto.AutoSubsystem;
import org.bitbuckets.lib.log.Debuggable;

public class ArmSubsystem {

    //make motors

    final ArmInput armInput;
    final ArmControl armControl;
    final Debuggable debuggable;
    final AutoSubsystem autoSubsystem;

    ArmFSM state = ArmFSM.DEFAULT; // Placeholder, default state
    ArmFSM nextState = ArmFSM.DEFAULT;

    public ArmSubsystem(ArmInput armInput, ArmControl armControl, Debuggable debuggable, AutoSubsystem autoSubsystem) {
        this.armInput = armInput;
        this.armControl = armControl;
        this.debuggable = debuggable;
        this.autoSubsystem = autoSubsystem;
    }


    //private double CONTROL_JOINT_OUTPUT = 0.1;

    //calculated gearRatio
    //private double gearRatio = (5 * 4 * 3) / (12. / 30.);


    public void loop() {
        switch (autoSubsystem.state()) {
            case AUTO_RUN:
                autoPeriodic();
            case TELEOP:
                teleopPeriodic();
        }
    }

    public void autoPeriodic() {
        if (autoSubsystem.sampleHasEventStarted("go-to-storage"))
        {
            state = ArmFSM.STORAGE;
        }
        if (autoSubsystem.sampleHasEventStarted("go-to-prepare"))
        {
            state = ArmFSM.PREPARE;
        }
        if (autoSubsystem.sampleHasEventStarted("score-high"))
        {
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

            //if C is pressed in sim (on keyboard)
            case STORAGE:

                //if X is pressed in sim (on keyboard)
                if (armInput.isStopPidPressed()) {
                    state = ArmFSM.TELEOP;
                }
                armControl.storeArm();
                if (armControl.isErrorSmallEnough(.1)) {
                    state = ArmFSM.TELEOP;
                }
                break;

            case PREPARE:
                armControl.prepareArm();
                if (armControl.isErrorSmallEnough(.1) || armInput.isStopPidPressed()){
                    state = nextState;
                }

                break;

            case HUMAN_INTAKE:
                armControl.humanIntake();
                if (armControl.isErrorSmallEnough(.1) || armInput.isStopPidPressed()) {
                    state = ArmFSM.TELEOP;
                }
                break;

            case SCORE_LOW:
                armControl.scoreLow();
                if (armControl.isErrorSmallEnough(0.1) || armInput.isStopPidPressed()) {
                    state = ArmFSM.TELEOP;
                }
                break;

            case SCORE_MID:
                armControl.scoreMid();
                if (armControl.isErrorSmallEnough(.1) || armInput.isStopPidPressed()){
                    state = ArmFSM.MANUAL;
                }
                break;

            case SCORE_HIGH:
                armControl.scoreHigh();
                if (armControl.isErrorSmallEnough(.1) || armInput.isStopPidPressed()){
                    state = ArmFSM.MANUAL;
                }
                break;
        }
        debuggable.log("state", state);
    }



}
