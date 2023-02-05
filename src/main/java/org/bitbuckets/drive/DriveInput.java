package org.bitbuckets.drive;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.filter.SlewRateLimiter;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.XboxController;
import org.bitbuckets.robot.XboxConstants;

public class DriveInput {

    final SlewRateLimiter x = new SlewRateLimiter(2);
    final SlewRateLimiter y = new SlewRateLimiter(2);

    final Joystick joystick;

    public DriveInput(Joystick joystick) {
        this.joystick = joystick;
    }

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
        return x.calculate(driveDeadband(joystick.getRawAxis(0)));
    }

    /**
     * @return the desired y velocity from joystick modified by an accel. limiter and a deadband
     * @units m/s
     */
    public double getInputY() {
        return y.calculate(driveDeadband(joystick.getRawAxis(1)));
    }

    /**
     * @return gets the user desired rotation with a deadband
     * @units unknown
     */
    public double getInputRot() {
        return driveDeadband(joystick.getRawAxis(XboxConstants.RIGHT_STICK_X));
    }

    /**
     * @return whether the slow drive (left trigger) is held down
     */
    public boolean isSlowDrivePressed() {
        return joystick.getRawAxis(XboxConstants.LEFT_TRIGGER) > 0.1;
    }

    /**
     * @return whether the aim drive is held or not
     */
    public boolean isAutoHeadingPressed() {

        return joystick.getRawButtonPressed(XboxConstants.CROSS);
    }

    public boolean isUserInputZeroed() {
        return getInputX() == 0 && getInputY() == 0 && getInputRot() == 0;
    }

    public boolean isDefaultPressed() {
        return joystick.getRawButtonPressed(XboxConstants.SQUARE);
    }

    public boolean isVisionGoPressed() {
        return joystick.getRawButtonPressed(XboxConstants.TRIANGLE);
    }

    public boolean isVisionGoReleased() {
        return joystick.getRawButtonReleased(XboxController.Button.kY.value);
    }


    public boolean isAutoBalancePressed() {
        return joystick.getRawButtonPressed(XboxConstants.CIRCLE);
    }

}