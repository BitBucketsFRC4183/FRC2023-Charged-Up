package org.bitbuckets.elevator;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.XboxController;
import org.bitbuckets.robot.XboxConstants;

public class ElevatorInput {
    final Joystick joystick;
    public ElevatorInput(Joystick joystick) {
        this.joystick = joystick;
    }

    public boolean getInputCirlce()
    {
        return joystick.getRawButtonPressed(XboxConstants.CIRCLE);
    }
    public boolean getInputDpadUp()
    {
        int pressed = joystick.getPOV();
        return pressed == 0;
    }
    public boolean getInputDpadDown()
    {
        int pressed = joystick.getPOV();
        return pressed == 180;
    }
    public boolean getInputDpadLeft()
    {
        int pressed = joystick.getPOV();
        return pressed == 270;
    }
    public boolean getInputDpadRight()
    {

        int pressed = joystick.getPOV();
        return pressed == 90;
    }


}
