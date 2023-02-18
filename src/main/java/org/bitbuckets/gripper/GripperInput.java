package org.bitbuckets.gripper;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.XboxController;

public class GripperInput {

    final Joystick operatorControl;

    public GripperInput(Joystick operatorControl) {
        this.operatorControl = operatorControl;
    }

    public boolean ifGripperPressed() {
        return operatorControl.getRawButton(XboxController.Button.kY.value);
    }
}
