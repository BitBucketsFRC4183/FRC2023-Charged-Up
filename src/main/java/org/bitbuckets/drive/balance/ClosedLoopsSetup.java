package org.bitbuckets.drive.balance;

import edu.wpi.first.math.controller.PIDController;
import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.ProcessPath;
import org.bitbuckets.lib.control.IPIDCalculator;
import org.bitbuckets.lib.control.PIDCalculatorSetup;
import org.bitbuckets.lib.control.PIDConfig;
import org.bitbuckets.lib.control.ProfiledPIDFController;

/**
 * labels: high priority
 * TODO use the IPidCalculator from mattlib
 */
public class ClosedLoopsSetup implements ISetup<ClosedLoopsControl> {
    @Override
    public ClosedLoopsControl build(ProcessPath self) {

        IPIDCalculator balanceController = new PIDCalculatorSetup(new PIDConfig(0.05,0,0,0)).build(self.addChild("balance-pid"));
        ProfiledPIDFController rotController = new ProfiledPIDFController(0,0,0,0, null);

        return new ClosedLoopsControl(balanceController,rotController);
    }
}
