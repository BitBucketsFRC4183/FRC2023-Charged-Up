package org.bitbuckets.auto;

import org.bitbuckets.lib.IProcess;
import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.ITuneAs;

/**
 * labels: low priority, easy
 * TODO: This should use {@link org.bitbuckets.lib.control.IPIDCalculator instead of PidController when it is done}
 */
public class AutoSubsystemSetup implements ISetup<AutoSubsystem> {

    final ISetup<IAutoControl> autoControlSetup;

    public AutoSubsystemSetup(ISetup<IAutoControl> autoControlSetup) {
        this.autoControlSetup = autoControlSetup;
    }

    @Override
    public AutoSubsystem build(IProcess self) {
        return new AutoSubsystem(
                self.generateTuner(ITuneAs.ENUM(AutoPath.class), "auto-path", AutoPath.NONE),
                self.childSetup("auto-control", autoControlSetup),
                self.getDebuggable()
        );
    }


}
