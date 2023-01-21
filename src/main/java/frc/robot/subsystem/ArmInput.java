package frc.robot.subsystem;

import edu.wpi.first.wpilibj.Joystick;
import frc.robot.Buttons;
import org.bitbuckets.robot.XboxConstants;

public class ArmInput {
    Joystick joystick = new Joystick(1);

    /*
    (A) Hold to deploy and run floor intake, release to retract
    (Y) Tap Face Button to switch to low position
    (B) Tap Face Button to switch to high position
    (X) Tap to align to human player station
    Left Joystick to control lower joint on arm
    Right Joystick to control upper joint on arm

     */

    public ArmInput(Joystick joystick)
    {
        this.joystick = joystick;
    }

    public double getLowerJointAngle_PercentOutput()
    {
        return joystick.getRawAxis(XboxConstants.LEFT_STICK_X);
    }


    public double getUpperJointAngle_PercentOutput()
    {
        return joystick.getRawAxis(XboxConstants.RIGHT_STICK_X);
    }

    public boolean getIfHighPosIsPressed()
    {
        return joystick.getRawButtonPressed(XboxConstants.CIRCLE);
    }

    public boolean getIfLowPosIsPressed()
    {
        return joystick.getRawButtonPressed(XboxConstants.TRIANGLE);
    }


}
