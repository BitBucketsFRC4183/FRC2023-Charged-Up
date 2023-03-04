package org.bitbuckets.drive.balance;

import org.bitbuckets.lib.IProcess;
import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.control.IPIDCalculator;

/**
 * labels: high priority
 * TODO use the IPidCalculator from mattlib
 */
public class BalanceSetup implements ISetup<BalanceControl> {

    final ISetup<IPIDCalculator> balanceCalculator;

    public BalanceSetup(ISetup<IPIDCalculator> balanceCalculator) {
        this.balanceCalculator = balanceCalculator;
    }

    @Override
    public BalanceControl build(IProcess self) {

        IPIDCalculator balanceController = self.childSetup("balance-pid", balanceCalculator);

        return new BalanceControl(balanceController);
    }
}
