package org.bitbuckets.auto;

import org.bitbuckets.lib.IProcess;
import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.ITuneAs;
import org.bitbuckets.lib.tune.IValueTuner;
import org.bitbuckets.lib.util.MockingUtil;

/**
 * labels: low priority, easy
 * TODO: This should use {@link org.bitbuckets.lib.control.IPIDCalculator instead of PidController when it is done}
 */
public class AutoSubsystemSetup implements ISetup<AutoSubsystem> {

    final boolean enabled;
    final ISetup<IAutoControl> autoControlSetup;

    public AutoSubsystemSetup(boolean enabled, ISetup<IAutoControl> autoControlSetup) {
        this.enabled = enabled;
        this.autoControlSetup = autoControlSetup;
    }

    @Override
    public AutoSubsystem build(IProcess self) {
        if (!enabled) return MockingUtil.buddy(AutoSubsystem.class);

        IAutoControl autoControl = self.childSetup("auto-control", autoControlSetup);
        IValueTuner<AutoPath> pathTuner = self.generateTuner(ITuneAs.ENUM(AutoPath.class), "auto-path", AutoPath.NONE);

        return new AutoSubsystem(pathTuner, autoControl, self.getDebuggable());
    }


}
