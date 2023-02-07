package org.bitbuckets.drive.balance;

import edu.wpi.first.math.controller.PIDController;
import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.control.ProfiledPIDFController;
import org.bitbuckets.lib.ProcessPath;

/**
 * labels: high priority
 * TODO use the IPidCalculator from mattlib
 */
public class ClosedLoopsSetup implements ISetup<ClosedLoopsControl> {
    @Override
    public ClosedLoopsControl build(ProcessPath path) {

        PIDController balanceController = new PIDController(0,0,0);
        ProfiledPIDFController rotController = new ProfiledPIDFController(0,0,0,0, null);

        return new ClosedLoopsControl(balanceController,rotController);
    }
}
