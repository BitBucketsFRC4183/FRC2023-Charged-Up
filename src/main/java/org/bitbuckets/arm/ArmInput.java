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


    public boolean isIntakeHumanPressed() {
        return operatorControl.getRawButtonPressed(XboxController.Button.kX.value);
    }

    public boolean isIntakeGroundPressed() {
        return operatorControl.getRawButtonPressed(XboxController.Button.kY.value);
    }


    //checks if the user wants to move the arms to the lower position (if y is pressed)
    public boolean isScoreMidPressed() {
        return operatorControl.getRawButtonPressed(XboxController.Button.kA.value);
    }

    //checks if the user wants to move the arms to the upper position (if b is pressed)
    public boolean isScoreHighPressed() {
        return operatorControl.getRawButtonPressed(XboxController.Button.kB.value);
    }

    //checks if the user wants to move the arms to the middle position (if x is pressed)

    public boolean isCalibratedPressed() {
        return operatorControl.getRawButtonPressed(XboxController.Button.kLeftStick.value);
    }

    public boolean isDisablePositionControlPressed() {
        return operatorControl.getRawButtonPressed(XboxController.Button.kRightStick.value);
    }


}
