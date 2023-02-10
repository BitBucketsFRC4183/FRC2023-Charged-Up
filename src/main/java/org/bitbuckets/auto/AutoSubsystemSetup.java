package org.bitbuckets.auto;

import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.ProcessPath;
import org.bitbuckets.lib.StartupProfiler;
import org.bitbuckets.lib.log.Debuggable;
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
    public AutoSubsystem build(ProcessPath self) {
        if (!enabled) return MockingUtil.buddy(AutoSubsystem.class);

        StartupProfiler p = self.generateSetupProfiler("some-ass");

        p.markProcessing();
        p.markErrored(new IllegalStateException("the robot exploded"));

        IAutoControl autoControl = new AutoControlSetup().build(self.addChild("auto-control"));
        IValueTuner<AutoPath> pathTuner = self.generateEnumTuner("path", AutoPath.class, AutoPath.NONE);
        Debuggable debuggable = self.generateDebugger();
        AutoSubsystem subsystem = new AutoSubsystem(pathTuner, autoControl, debuggable);

        self.registerLogicLoop(subsystem::logLoop);

        return subsystem;
    }


}
