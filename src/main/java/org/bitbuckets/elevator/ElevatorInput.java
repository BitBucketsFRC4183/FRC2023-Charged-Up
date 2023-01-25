package org.bitbuckets.elevator;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj2.command.button.POVButton;
import org.bitbuckets.robot.XboxConstants;

public class ElevatorInput {
    final Joystick joystick;

    public ElevatorInput(Joystick joystick) {
        this.joystick = joystick;
    }

    public boolean getInputDpadUp()
    {
        return new POVButton(joystick,180).getAsBoolean(); // DPAD up
    }
    public boolean getInputDpadDown()
    {
        return new POVButton(joystick,0).getAsBoolean(); // DPAD up
    }
    public boolean getInputDpadLeft()
    {
        return new POVButton(joystick,270).getAsBoolean(); // DPAD up
    }
    public boolean getInputDpadRight()
    {
        return new POVButton(joystick,90).getAsBoolean(); // DPAD up
    }


}
