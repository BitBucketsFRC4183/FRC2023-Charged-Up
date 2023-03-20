package org.bitbuckets;

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
     * @return the desired x velocity from joystick modified by an accel. limiter and a deadband
     * @units m/s
     */
    public double getInputX() {
        return x.calculate(driveDeadband(driveControl.getRawAxis(0)));
    }

    /**
     * @return the desired y velocity from joystick modified by an accel. limiter and a deadband
     * @units m/s
     */
    public double getInputY() {
        return -y.calculate(driveDeadband(driveControl.getRawAxis(1)));
    }

    /**
     * @return gets the user desired rotation with a deadband
     * @units unknown
     */
    public double getInputRot() {
        return driveDeadband(driveControl.getRawAxis(4));
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
        return getInputX() == 0 && getInputY() == 0 && getInputRot() == 0;
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

    /*
    Left d-pad: store
    Right d-pad: mid
    Up d-pad: high
    Low d-pad: low

     */

    // checks if operator wants to move arms to store position (by pressing LEFT DPAD)
    public boolean isStoragePressed() {
        int pressed = operatorControl.getPOV();
        return pressed == 270;
    }


    // checks if operator wants to move arms to score in low node position (by pressing DOWN DPAD)
    public boolean isScoreLowPressed() {
        int pressed = operatorControl.getPOV();
        return pressed == 180;
    }

    // checks if operator wants to move arms to score in medium node position (by pressing RIGHT DPAD)
    public boolean isScoreMidPressed() {
        int pressed = operatorControl.getPOV();
        return pressed == 90;
    }

    // checks if operator wants to move arms to score in high node position (by pressing UP DPAD)
    public boolean isScoreHighPressed() {
        int pressed = operatorControl.getPOV();
        return pressed == 0;
    }


    public boolean isStopPidPressed() {
        return operatorControl.getRawButtonPressed(XboxController.Button.kB.value);
    }

    public boolean intakeGripper() {
        return operatorControl.getRawButton(XboxController.Button.kRightBumper.value);
    }

    public boolean outtakeGripper() {
        return operatorControl.getRawButton(XboxController.Button.kLeftBumper.value);
    }

    public boolean isCube() {
        return operatorControl.getRawButton(XboxController.Button.kRightStick.value);
    }

    public boolean stopStickyPressed() {
        return driveControl.getRawButton(XboxController.Button.kX.value);
    }


    public boolean isLoadPresed() {
        return operatorControl.getRawButtonPressed(XboxController.Button.kX.value);
    }
    public boolean openGripper() {
        return operatorControl.getRawButton(XboxController.Button.kY.value);
    }

}
