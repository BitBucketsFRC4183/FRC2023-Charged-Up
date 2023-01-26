package org.bitbuckets.drive.balance;

import edu.wpi.first.math.controller.PIDController;
import org.bitbuckets.lib.control.ProfiledPIDFController;

public class AutoAxisControl {

    final PIDController balanceController;
    final ProfiledPIDFController rotController;

    AutoAxisControl(PIDController balanceController, ProfiledPIDFController rotController) {
        this.balanceController = balanceController;
        this.rotController = rotController;
    }

    public double calculateBalanceOutput(double roll_deg, double setpoint) {
        return balanceController.calculate(roll_deg, setpoint);
    }



    public double calculateRotOutputRad(double imu_yaw, double setpoint) {
        rotController.enableContinuousInput(0, Math.toRadians(360));
        return rotController.calculate(imu_yaw, setpoint);
    }

}
