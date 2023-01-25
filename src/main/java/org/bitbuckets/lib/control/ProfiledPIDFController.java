package org.bitbuckets.lib.control;

import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.trajectory.TrapezoidProfile;


/**
 * tags: low priority
 * TODO document
 */
public class ProfiledPIDFController extends ProfiledPIDController {

    final double Kf;
    public ProfiledPIDFController(double Kp, double Ki, double Kd, double Kf, TrapezoidProfile.Constraints constraints) {
        super(Kp, Ki, Kd, constraints);
        this.Kf = Kf;
    }

    @Override
    public double calculate(double measurement, double setpoint) {
        return super.calculate(measurement, setpoint) + Kf * setpoint;
    }
}
