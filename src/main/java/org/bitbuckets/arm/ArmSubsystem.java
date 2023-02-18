package org.bitbuckets.arm;

import org.bitbuckets.gripper.GripperControl;
import org.bitbuckets.gripper.GripperFSM;
import org.bitbuckets.lib.log.Debuggable;

public class ArmSubsystem {

    //make motors


    final ArmInput armInput;
    final ArmControl armControl;

    final GripperControl gripperControl;
    final Debuggable debuggable;

    ArmFSM state = ArmFSM.MANUAL;
    ArmFSM nextState = ArmFSM.MANUAL;
    GripperFSM gripperState = GripperFSM.MANUAL;

    public ArmSubsystem(ArmInput armInput, ArmControl armControl, GripperControl gripperControl, Debuggable debuggable) {
        this.armInput = armInput;
        this.armControl = armControl;
        this.gripperControl = gripperControl;
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
        //if (armInput.isDisablePositionControlPressed()) {
        //    state = ArmFSM.MANUAL;
        //}

        switch (state) {
            case MANUAL:
                armControl.manuallyMoveLowerArm(armInput.getLowerArm_PercentOutput());
                armControl.manuallyMoveUpperArm(armInput.getUpperArm_PercentOutput());
                gripperControl.closeGripper();

                debuggable.log("line 49", true);

                if (armInput.isStoragePressed()) {
                    state = ArmFSM.STORAGE;
                } else if (armInput.isHumanIntakePressed()) {
                    state = ArmFSM.PREPARE;
                    nextState = ArmFSM.HUMAN_INTAKE;
                    gripperState = GripperFSM.OPEN;
                    gripperControl.openGripper();
                } else if (armInput.isScoreMidPressed()) {
                    debuggable.log("line 55", true);
                    state = ArmFSM.PREPARE;
                    nextState = ArmFSM.SCORE_MID;
                    gripperState = GripperFSM.OPEN;
                    gripperControl.openGripper();
                } else if (armInput.isScoreHighPressed()) {
                    state = ArmFSM.PREPARE;
                    nextState = ArmFSM.SCORE_HIGH;
                    gripperState = GripperFSM.OPEN;
                    gripperControl.openGripper();
                } else if (armInput.isScoreLowPressed()) {
                    state = ArmFSM.PREPARE;
                    nextState = ArmFSM.SCORE_LOW;
                    gripperState = GripperFSM.OPEN;
                    gripperControl.openGripper();
                }
                break;

            case STORAGE:
                armControl.storeArm();
                gripperState = GripperFSM.CLOSE;
                gripperControl.closeGripper();
                if (!armInput.isStoragePressed()) {
                    state = ArmFSM.MANUAL;
                }
                break;

            case PREPARE:
                armControl.prepareArm();
                if (armControl.isErrorSmallEnough(3.69)) {
                    state = nextState;
                    gripperState = GripperFSM.OPEN;
                    gripperControl.openGripper();
                }
                break;

            case HUMAN_INTAKE:
                armControl.humanIntake();
                if (armControl.isErrorSmallEnough(3.69)) {
                    gripperState = GripperFSM.OPEN;
                    gripperControl.openGripper();
                    state = ArmFSM.MANUAL;

                }
                break;

            case SCORE_LOW:
                armControl.scoreLow();
                if (armControl.isErrorSmallEnough(3.69)) {
                    gripperState = GripperFSM.OPEN;
                    gripperControl.openGripper();
                    state = ArmFSM.MANUAL;

                }
                break;

            case SCORE_MID:
                armControl.scoreMid();
                if (armControl.isErrorSmallEnough(3.69)) {
                    gripperState = GripperFSM.OPEN;
                    gripperControl.openGripper();
                    state = ArmFSM.MANUAL;

                }
                break;

            case SCORE_HIGH:
                armControl.scoreHigh();
                if (armControl.isErrorSmallEnough(3.69)) {
                    gripperState = GripperFSM.OPEN;
                    gripperControl.openGripper();
                    state = ArmFSM.MANUAL;

                }
                break;
        }
        debuggable.log("state", state);
    }


}
