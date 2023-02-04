package org.bitbuckets.elevator;

import edu.wpi.first.wpilibj.Joystick;
import org.bitbuckets.robot.XboxConstants;

public class ElevatorInput {
    final Joystick joystick;

    public ElevatorInput(Joystick joystick) {
        this.joystick = joystick;
    }

    public boolean getInputCirlce() {
        return joystick.getRawButton(XboxConstants.CIRCLE);
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

    public boolean getInputDpadZero() {

        int pressed = joystick.getPOV();
        return pressed == -1;
    }

    public boolean getInputSquare() {
        return joystick.getRawButtonPressed(XboxConstants.SQUARE);
    }

    public boolean getInputLeftStick() {
        return joystick.getRawButtonPressed(XboxConstants.L_STICK);
    }


}
