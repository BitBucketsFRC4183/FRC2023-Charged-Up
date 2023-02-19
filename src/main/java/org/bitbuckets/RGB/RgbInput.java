package org.bitbuckets.RGB;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.XboxController;

public class RgbInput {
    final Joystick operatorControl;


    public RgbInput(Joystick operatorControl) {
        this.operatorControl = operatorControl;
    }


    public boolean buttonA() {
        return operatorControl.getRawButton(XboxController.Button.kA.value);
    }

    public boolean buttonB() {
        return operatorControl.getRawButton(XboxController.Button.kB.value);
    }


}
