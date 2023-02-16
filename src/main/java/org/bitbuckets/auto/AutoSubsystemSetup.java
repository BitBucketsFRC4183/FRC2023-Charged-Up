package org.bitbuckets.auto;

import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.log.IDebuggable;
import org.bitbuckets.lib.IProcess;
import org.bitbuckets.lib.tune.IValueTuner;
import org.bitbuckets.lib.util.MockingUtil;

/**
 * labels: low priority, easy
 * TODO: This should use {@link org.bitbuckets.lib.control.IPIDCalculator instead of PidController when it is done}
 */
public class AutoSubsystemSetup implements ISetup<AutoSubsystem> {

    final boolean enabled;


    public AutoSubsystemSetup(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public AutoSubsystem build(IProcess self) {
        if (!enabled) return MockingUtil.buddy(AutoSubsystem.class);


        IAutoControl autoControl = self.childSetup("auto-control", new AutoControlSetup());
        IValueTuner<AutoPath> pathTuner = self.generateTuner(AutoPath.class, "path", AutoPath.NONE);
        IDebuggable debuggable = self.getDebuggable();
        AutoSubsystem subsystem = new AutoSubsystem(pathTuner, autoControl, debuggable);

        self.registerLogLoop(subsystem::logLoop);

        return subsystem;
    }


}
