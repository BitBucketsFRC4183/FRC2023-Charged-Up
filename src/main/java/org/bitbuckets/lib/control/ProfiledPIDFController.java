package org.bitbuckets.lib.control;

import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.trajectory.TrapezoidProfile;

public class ProfiledPIDFController extends ProfiledPIDController {

    //
    final double Kf;

    /*
    PIDF Controller that requires a max acceleration and velocity for tuning PIDF
     */
    public ProfiledPIDFController(double Kp, double Ki, double Kd, double Kf, TrapezoidProfile.Constraints constraints) {
        super(Kp, Ki, Kd, constraints);
        this.Kf = Kf;
    }

    /*
    Actually calculates the output the PIDF returns based on current position/angle and setpoint
     */
    @Override
    public double calculate(double measurement, double setpoint) {
        return super.calculate(measurement, setpoint) + Kf * setpoint;
    }
}
