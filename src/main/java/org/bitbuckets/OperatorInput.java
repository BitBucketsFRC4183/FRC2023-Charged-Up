package org.bitbuckets;

import config.Drive;
import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.filter.SlewRateLimiter;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.XboxController;

public class OperatorInput {

    final Joystick operatorControl;
    final Joystick driveControl;

    final SlewRateLimiter x = new SlewRateLimiter(2);
    final SlewRateLimiter y = new SlewRateLimiter(2);


    public OperatorInput(Joystick operatorControl, Joystick driveControl) {
        this.operatorControl = operatorControl;
        this.driveControl = driveControl;
    }


    //DRIVER

    /**
     * @param input a value
     * @return that value, deadbanded
     */
    static double driveDeadband(double input) {
        double value = input;

        value = MathUtil.applyDeadband(value, 0.1);
        value = Math.copySign(value * value, value);


        return value;
    }

    /**
     * @return the desired field relative Y velocity from joystick x axis modified by an accel. limiter and a deadband
     * @units m/s
     */
    public double getDesiredY_fieldRelative() {
        if (isSlowDrivePressed()) {
            return driveDeadband(
                    driveControl.getRawAxis(0) //This is the X axis on the joystick, which corresponds to the field relative Y
            ) * Drive.SLOW_MODE_COEFFICIENT;
        } else {
            return driveDeadband(
                    driveControl.getRawAxis(0)
            );
        }

    }

    /**
     * @return the desired field relative x velocity from joystick y axis modified by an accel. limiter and a deadband
     * @units m/s
     */
    public double getDesiredX_fieldRelative() {
        if (isSlowDrivePressed()) {
            return driveDeadband(
                    driveControl.getRawAxis(1)
            ) * Drive.SLOW_MODE_COEFFICIENT;
        } else {
            return driveDeadband(
                    driveControl.getRawAxis(1)
            );
        }
    }

    /**
     * @return gets the user desired rotation with a deadband
     * @units unknown
     */
    public double getDesiredRotation_initializationRelative() {

        if (isSlowDrivePressed()) {
            return driveDeadband(driveControl.getRawAxis(4)) * Drive.SLOW_MODE_COEFFICIENT;
        } else {
            return driveDeadband(driveControl.getRawAxis(4));
        }

    }

    /**
     * @return whether the slow drive (left trigger) is held down
     */
    public boolean isSlowDrivePressed() {
        return driveControl.getRawAxis(XboxController.Axis.kLeftTrigger.value) > 0.1;
    }

    /**
     * @return whether the aim drive is held or not
     */
    public boolean isAutoHeadingPressed() {
        return driveControl.getRawButtonPressed(XboxController.Button.kA.value);
    }

    public boolean isUserInputNone() {
        return getDesiredY_fieldRelative() == 0 && getDesiredX_fieldRelative() == 0 && getDesiredRotation_initializationRelative() == 0;
    }

    public boolean isVisionDrivePressed() {
        return driveControl.getRawButton(XboxController.Button.kLeftStick.value);
    }

    public boolean isAutoBalancePressed() {
        return driveControl.getRawButton(XboxController.Button.kB.value);
    }

    public boolean isResetGyroPressed() {
        return driveControl.getRawButtonPressed(XboxController.Button.kStart.value);
    }

    public boolean isManualDrivePressed() {
        return driveControl.getRawButtonPressed(XboxController.Button.kX.value);
    }

    //OPERATOR

    public static double armDeadband(double input) {
        double value = input;

        value = MathUtil.applyDeadband(value, 0.1);
        value = Math.copySign(value * value, value);

        return value;
    }

    public double getLowerArm_PercentOutput() {

        return armDeadband(operatorControl.getRawAxis(XboxController.Axis.kLeftY.value)); //TODO remove 0.5 because iw as using it for testing in order to not explode robot
    }

    public double getUpperArm_PercentOutput() {

        return -armDeadband(operatorControl.getRawAxis(XboxController.Axis.kRightY.value));
    }

    public boolean isHumanIntakePressed() {
        return operatorControl.getRawButton(XboxController.Button.kX.value);
    }



    public boolean isManualModePressed() {
        return operatorControl.getRawButtonPressed(XboxController.Button.kA.value);
    }

    public boolean isZeroArmPressed() {
        return operatorControl.getRawButtonPressed(XboxController.Button.kLeftStick.value);
    }

    // checks if operator wants to move arms to intake for human player station (by pressing RIGHT DPAD)
    public boolean isStoragePressed() {
        int pressed = operatorControl.getPOV();
        return pressed == 270;
    }


    //controlled by dPad Left
    public boolean isScoreLowPressed() {
        int pressed = operatorControl.getPOV();
        return pressed == 180;
    }

    // checks if operator wants to move arms to score in medium node position (by pressing DPad Down)
    public boolean isScoreMidPressed() {
        int pressed = operatorControl.getPOV();
        return pressed == 90;
    }

    // checks if operator wants to move arms to score in high node position (by pressing dPad Up)
    public boolean isScoreHighPressed() {
        int pressed = operatorControl.getPOV();
        return pressed == 0;
    }


    public boolean isStopPidPressed() {
        return operatorControl.getRawButtonPressed(XboxController.Button.kB.value);
    }

    public boolean openGripperPressed() {
        return operatorControl.getRawButton(XboxController.Button.kRightBumper.value);
    }

    public boolean closeGripperPressed() {
        return operatorControl.getRawButton(XboxController.Button.kLeftBumper.value);
    }

    public boolean conevscube() {
        return false;//operatorControl.getRawButton(XboxController.Button.kLeftBumper.value);
    }

    public boolean stopStickyPressed() {
        return driveControl.getRawButton(XboxController.Button.kX.value);
    }

    public boolean zeroGripper() {
        return operatorControl.getRawButton(XboxController.Button.kRightStick.value);
    }

    public boolean isLoadPresed(){
        return operatorControl.getRawButtonPressed(XboxController.Button.kX.value);
    }

}
