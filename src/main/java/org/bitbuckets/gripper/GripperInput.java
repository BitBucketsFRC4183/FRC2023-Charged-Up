package org.bitbuckets.gripper;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.XboxController;

public class GripperInput {

    final Joystick operatorControl;

    public GripperInput(Joystick operatorControl) {
        this.operatorControl = operatorControl;
    }

    public boolean ifOpenGripperPressed() {
        return operatorControl.getRawButton(XboxController.Button.kRightBumper.value);
    }

    public boolean ifCloseGripperPressed(){
        return operatorControl.getRawButton(XboxController.Button.kLeftBumper.value);
    }
}
