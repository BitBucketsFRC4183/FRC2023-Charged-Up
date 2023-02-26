package org.bitbuckets.drive.balance;

import edu.wpi.first.math.controller.HolonomicDriveController;
import edu.wpi.first.math.controller.PIDController;
import org.bitbuckets.lib.control.IPIDCalculator;
import org.bitbuckets.lib.control.ProfiledPIDFController;

public class ClosedLoopsControl {

    final IPIDCalculator balanceController;
    final ProfiledPIDFController rotController;


    ClosedLoopsControl(IPIDCalculator balanceController, ProfiledPIDFController rotController) {
        this.balanceController = balanceController;
        this.rotController = rotController;
    }

    public double calculateBalanceOutput(double roll_deg, double setpoint) {
        return balanceController.calculateNext(roll_deg, setpoint);
    }

    public double calculateRotOutputRad(double imu_yaw, double setpoint) {
        rotController.enableContinuousInput(0, Math.toRadians(360));
        return rotController.calculate(imu_yaw, setpoint);
    }

}
