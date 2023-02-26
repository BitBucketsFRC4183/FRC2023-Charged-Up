package org.bitbuckets.drive.balance;

import edu.wpi.first.math.controller.PIDController;
import org.bitbuckets.lib.IProcess;
import org.bitbuckets.lib.ISetup;

import org.bitbuckets.lib.control.IPIDCalculator;
import org.bitbuckets.lib.control.PIDCalculatorSetup;
import org.bitbuckets.lib.control.PIDConfig;
import org.bitbuckets.lib.control.ProfiledPIDFController;

import java.util.Optional;

/**
 * labels: high priority
 * TODO use the IPidCalculator from mattlib
 */
public class BalanceSetup implements ISetup<BalanceControl> {

    @Override
    public BalanceControl build(IProcess self) {

        IPIDCalculator balanceController = self.childSetup("pid", new PIDCalculatorSetup(new PIDConfig(0.05,0,0, Optional.empty(), Optional.empty())));
        ProfiledPIDFController rotController = new ProfiledPIDFController(0,0,0,0, null);

        return new BalanceControl(balanceController,rotController);
    }
}
