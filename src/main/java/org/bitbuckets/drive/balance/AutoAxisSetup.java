package org.bitbuckets.drive.balance;

import edu.wpi.first.math.controller.PIDController;
import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.ProcessPath;
import org.bitbuckets.lib.control.ProfiledPIDFController;

/**
 * labels: high priority
 * TODO use the IPidCalculator from mattlib
 */
public class AutoAxisSetup implements ISetup<AutoAxisControl> {
    @Override
    public AutoAxisControl build(ProcessPath path) {

        PIDController balanceController = new PIDController(0,0,0);
        ProfiledPIDFController rotController = new ProfiledPIDFController(0,0,0,0, null);

        return new AutoAxisControl(balanceController,rotController);
    }
}
