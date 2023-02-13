package org.bitbuckets.macros;


import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.XboxController;
import org.bitbuckets.lib.log.Debuggable;

public class MacroInput {

    final Joystick operatorControl;
    final Debuggable debug;


    public MacroInput(Joystick operatorControl, Debuggable debug) {
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
