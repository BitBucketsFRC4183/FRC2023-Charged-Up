package org.bitbuckets.gripper;

import org.bitbuckets.arm.ArmFSM;

public class GripperSubsystem {

    final GripperControl gripperControl;

    ArmFSM armState = ArmFSM.MANUAL;

    GripperFSM state = GripperFSM.MANUAL;

    public GripperSubsystem(GripperControl gripperControl) {
        this.gripperControl = gripperControl;
    }

    public void teleopPeriodic(){

        switch (state){
            case OPEN:
                if((armState == ArmFSM.SCORE_LOW) || (armState == ArmFSM.SCORE_MID) || (armState == ArmFSM.SCORE_HIGH)){
                    gripperControl.openGripper();
                }

            case CLOSE:

                gripperControl.closeGripper();
        }
    }
}
