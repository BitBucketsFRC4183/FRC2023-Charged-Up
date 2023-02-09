package org.bitbuckets.arm;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.XboxController;


public class ArmInput {

    final Joystick operatorControl;

    public ArmInput(Joystick operatorControl) {
        this.operatorControl = operatorControl;
    }

    /*
    (A) Hold to deploy and run floor intake, release to retract
    (Y) Tap Face Button to switch to low position
    (B) Tap Face Button to switch to high position
    (X) Tap to align to human player station
    Left Joystick to control lower joint on arm
    Right Joystick to control upper joint on arm
     */
    public static double armDeadband(double input) {
        double value = input;

        value = MathUtil.applyDeadband(value, 0.1);
        value = Math.copySign(value * value, value);

        return value;
    }

    public double getLowerArm_PercentOutput() {

        return armDeadband(operatorControl.getRawAxis(XboxController.Axis.kLeftY.value));
    }


    //how fast the user wants to move the upper arm (controlled by right trigger)
    public double getUpperArm_PercentOutput() {

        return armDeadband(operatorControl.getRawAxis(XboxController.Axis.kRightY.value));
    }

    //checks if the user wants to move the upper arm back (if the right bumper is held)


    public boolean isStoragePressed()
    {
        return operatorControl.getRawButtonPressed(XboxController.Button.kRightBumper.value);
    }

    // checks if operator wants to move arms to intake for human player station (by pressing X)
    public boolean isHumanIntakePressed() {
        return operatorControl.getRawButtonPressed(XboxController.Button.kX.value);
    }

    // checks if operator wants to move arms to score in medium node position (by pressing Y)
    public boolean isScoreMidPressed() {
        return operatorControl.getRawButtonPressed(XboxController.Button.kY.value);
    }

    // checks if operator wants to move arms to score in high node position (by pressing A)
    public boolean isScoreHighPressed() {
        return operatorControl.getRawButtonPressed(XboxController.Button.kA.value);
    }


    // sets current lowerArm and upperArm position to 0 when left joystick button is pressed
    public boolean isCalibratedPressed() {
        return operatorControl.getRawButtonPressed(XboxController.Button.kLeftStick.value);
    }

    public boolean isDisablePositionControlPressed() {
        return operatorControl.getRawButtonPressed(XboxController.Button.kRightStick.value);
    }


}
