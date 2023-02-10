package org.bitbuckets.elevator;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.XboxController;
import org.bitbuckets.lib.util.MathUtil;

public class ElevatorInput {
    final Joystick joystick;

    public ElevatorInput(Joystick joystick) {
        this.joystick = joystick;
    }


    public boolean getInputDpadUp() {
        int pressed = joystick.getPOV();
        return pressed == 0;
    }

    public boolean getInputDpadDown() {
        int pressed = joystick.getPOV();
        return pressed == 180;
    }

    public boolean getInputDpadLeft() {
        int pressed = joystick.getPOV();
        return pressed == 270;
    }

    public boolean getInputDpadRight() {

        int pressed = joystick.getPOV();
        return pressed == 90;
    }


    public boolean getInputA() {
        return joystick.getRawButton(XboxController.Button.kA.value);
    }

    public boolean getInputX() {
        return joystick.getRawButton(XboxController.Button.kX.value);
    }

    public double getLJoystickY() {
        return MathUtil.deadband(joystick.getRawAxis(XboxController.Axis.kLeftY.value));
    }

    public double getRJoystickX() {
        return MathUtil.deadband(joystick.getRawAxis(XboxController.Axis.kRightX.value));
    }


}
