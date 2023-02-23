package org.bitbuckets.arm;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.XboxController;
import org.bitbuckets.lib.log.Debuggable;


public class ArmInput {

    final Joystick operatorControl;
    final Debuggable debug;

    /**
     * Arm input, has a ton of functions for buttons and axis.
     * @param operatorControl
     * @param debug
     */
    public ArmInput(Joystick operatorControl, Debuggable debug) {
        this.operatorControl = operatorControl;
        this.debug = debug;
    }


    /**
     * Quick Buttons and Axis reference for Arm Controls
     * (X) tap to go in storage position (default during teleop)
     * (B) To stop all arm motors
     * (Right Trigger) Tap to grab with arm - clamp down
     * (Left trigger) Tap to grab with arm - release clamp
     * Dpad up – tap to score high
     * Dpad down – tap to score middle
     * Dpad right – tap for arm to go to load zone and intake GP
     * Dpad left – tap to score low
     * Right bumper – align with scoring zone
     * Left bumper – align with loading zone
     * Left Joystick  to control lower joint on arm
     * Right Joystick to control upper joint on arm
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

    public double getUpperArm_PercentOutput() {

        return -armDeadband(operatorControl.getRawAxis(5));
    }

    //for picking up cones
    public double getIsClampReleased() {
        return armDeadband(operatorControl.getRawAxis(XboxController.Axis.kLeftTrigger.value));
    }

    public double getIsClampHeld() {
        return armDeadband(operatorControl.getRawAxis(XboxController.Axis.kRightTrigger.value));
    }

    /*
    public double isStopAllMotorsPressed(){
        return operatorControl.getRawButtonPressed(XboxController.Button.kB.value);
    }
     */

    public boolean isStoragePressed() {
        return operatorControl.getRawButton(XboxController.Button.kX.value);
    }

    // checks if operator wants to move arms to intake for human player station (by pressing RIGHT DPAD)
    public boolean isHumanIntakePressed() {
        int pressed = operatorControl.getPOV();
        return pressed == 90;
    }


    //controlled by dPad Left
    public boolean isScoreLowPressed() {
        int pressed = operatorControl.getPOV();
        return pressed == 270;
    }

    // checks if operator wants to move arms to score in medium node position (by pressing DPad Down)
    public boolean isScoreMidPressed() {
        int pressed = operatorControl.getPOV();
        return pressed == 180;
    }

    // checks if operator wants to move arms to score in high node position (by pressing dPad Up)
    public boolean isScoreHighPressed() {
        int pressed = operatorControl.getPOV();
        return pressed == 0;
    }


    // sets current lowerArm and upperArm position to 0 when left joystick button is pressed
    public boolean isCalibratedPressed() {
        return operatorControl.getRawButtonPressed(XboxController.Button.kLeftStick.value);
    }

    public boolean isDisablePositionControlPressed() {
        return operatorControl.getRawButtonPressed(XboxController.Button.kRightStick.value);
    }


    public boolean isStopPidPressed() {
        return operatorControl.getRawButtonPressed(XboxController.Button.kB.value);
    }


}
