package org.bitbuckets.arm;

import org.bitbuckets.gripper.GripperControl;
import org.bitbuckets.gripper.GripperInput;
import org.bitbuckets.lib.log.Debuggable;

public class ArmSubsystem {

    //make motors

    final ArmInput armInput;
    final ArmControl armControl;

    final GripperControl gripperControl;
    final GripperInput gripperInput;
    final Debuggable debuggable;

    ArmFSM state = ArmFSM.MANUAL;
    ArmFSM nextState = ArmFSM.MANUAL;

    public ArmSubsystem(ArmInput armInput, ArmControl armControl, GripperControl gripperControl, GripperInput gripperInput, Debuggable debuggable) {
        this.armInput = armInput;
        this.armControl = armControl;
        this.gripperControl = gripperControl;
        this.gripperInput = gripperInput;
        this.debuggable = debuggable;
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
        }

        switch (state) {
            case MANUAL:
                armControl.manuallyMoveLowerArm(armInput.getLowerArm_PercentOutput());
                armControl.manuallyMoveUpperArm(armInput.getUpperArm_PercentOutput());
                if (gripperInput.ifGripperPressed()) {
                    gripperControl.openGripper();
                } else {
                    gripperControl.closeGripper();
                }

                debuggable.log("line 49", true);

                if (armInput.isStoragePressed()) {
                    state = ArmFSM.STORAGE;
                } else if (armInput.isHumanIntakePressed()) {
                    state = ArmFSM.PREPARE;
                    nextState = ArmFSM.HUMAN_INTAKE;
                    gripperControl.openGripper();
                } else if (armInput.isScoreMidPressed()) {
                    debuggable.log("line 55", true);
                    state = ArmFSM.PREPARE;
                    nextState = ArmFSM.SCORE_MID;
                    gripperControl.openGripper();
                } else if (armInput.isScoreHighPressed()) {
                    state = ArmFSM.PREPARE;
                    nextState = ArmFSM.SCORE_HIGH;
                    gripperControl.openGripper();
                } else if (armInput.isScoreLowPressed()) {
                    state = ArmFSM.PREPARE;
                    nextState = ArmFSM.SCORE_LOW;
                    gripperControl.openGripper();
                }
                break;

            //if C is pressed in sim (on keyboard)
            case STORAGE:

                //if X is pressed in sim (on keyboard)
                if (armInput.isStopPidPressed()) {
                    state = ArmFSM.MANUAL;
                }
                armControl.storeArm();
                gripperControl.closeGripper();
                if (armControl.isErrorSmallEnough(.1)) {
                    state = ArmFSM.MANUAL;
                }
                break;

            case PREPARE:
                armControl.prepareArm();
                if (armControl.isErrorSmallEnough(.1) || armInput.isStopPidPressed()){
                    state = nextState;
                    gripperControl.openGripper();
                }

                break;

            case HUMAN_INTAKE:
                armControl.humanIntake();
                if (armControl.isErrorSmallEnough(.1) || armInput.isStopPidPressed()) {
                    gripperControl.openGripper();
                    state = ArmFSM.MANUAL;

                }
                break;

            case SCORE_LOW:
                armControl.scoreLow();
                if (armControl.isErrorSmallEnough(0.1) || armInput.isStopPidPressed()) {
                    gripperControl.openGripper();
                    state = ArmFSM.MANUAL;

                }
                break;

            case SCORE_MID:
                armControl.scoreMid();
                if (armControl.isErrorSmallEnough(.1) || armInput.isStopPidPressed()) {
                    gripperControl.openGripper();
                    state = ArmFSM.MANUAL;

                }
                break;

            case SCORE_HIGH:
                armControl.scoreHigh();
                if (armControl.isErrorSmallEnough(.1) || armInput.isStopPidPressed()) {
                    gripperControl.openGripper();
                    state = ArmFSM.MANUAL;

                }
                break;
        }
        debuggable.log("state", state);
    }


}
