package org.bitbuckets.gripper;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.XboxController;

public class GripperInput {

    final Joystick operatorControl;

    GripperFSM gripperState;

    public GripperInput(Joystick operatorControl, GripperFSM gripperState) {
        this.operatorControl = operatorControl;
        this.gripperState = gripperState;
    }

    public boolean ifGripperPressed() {
        return operatorControl.getRawButton(XboxController.Button.kY.value);
    }
}
