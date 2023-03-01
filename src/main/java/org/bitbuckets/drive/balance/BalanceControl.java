package org.bitbuckets.drive.balance;

import org.bitbuckets.lib.control.IPIDCalculator;

public class BalanceControl {

    final IPIDCalculator balanceController;


    BalanceControl(IPIDCalculator balanceController) {
        this.balanceController = balanceController;
    }

    public double calculateBalanceOutput(double roll_deg, double setpoint) {
        return balanceController.calculateNext(roll_deg, setpoint);
    }



}
