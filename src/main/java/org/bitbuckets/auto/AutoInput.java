package org.bitbuckets.auto;


import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.XboxController;
import org.bitbuckets.lib.log.Debuggable;

public class AutoInput {

    final Joystick operatorControl;
    final Debuggable debug;


    public AutoInput(Joystick operatorControl, Debuggable debug) {
        this.operatorControl = operatorControl;
        this.debug = debug;
    }

    public boolean getLStickPressed() {
        return operatorControl.getRawButton(XboxController.Button.kLeftStick.value);
    }

    public boolean getRStickPressed() {
        return operatorControl.getRawButton(XboxController.Button.kRightStick.value);
    }

}
