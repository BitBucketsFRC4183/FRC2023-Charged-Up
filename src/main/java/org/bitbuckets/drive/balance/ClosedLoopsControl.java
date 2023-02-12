package org.bitbuckets.drive.balance;

import edu.wpi.first.math.controller.PIDController;
import org.bitbuckets.lib.control.ProfiledPIDFController;

public class ClosedLoopsControl {

    final PIDController balanceController;
    final ProfiledPIDFController rotController;


    ClosedLoopsControl(PIDController balanceController, ProfiledPIDFController rotController) {
        this.balanceController = balanceController;
        this.rotController = rotController;
    }

    public double calculateBalanceOutput(double pitch_deg, double setpoint) {
        return balanceController.calculate(pitch_deg, setpoint);
    }

    public double calculateRotOutputRad(double imu_yaw, double setpoint) {
        rotController.enableContinuousInput(0, Math.toRadians(360));
        return rotController.calculate(imu_yaw, setpoint);
    }

}
