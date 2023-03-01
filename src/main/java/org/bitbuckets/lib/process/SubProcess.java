package org.bitbuckets.lib.process;

import edu.wpi.first.wpilibj.shuffleboard.BuiltInLayouts;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardContainer;
import org.bitbuckets.lib.ILogAs;
import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.ITuneAs;
import org.bitbuckets.lib.ProcessMode;
import org.bitbuckets.lib.core.HasLifecycle;
import org.bitbuckets.lib.core.HasLogLoop;
import org.bitbuckets.lib.core.HasLoop;
import org.bitbuckets.lib.core.Path;
import org.bitbuckets.lib.debug.IDebuggable;
import org.bitbuckets.lib.debug.ShuffleDebuggable;
import org.bitbuckets.lib.log.IConsole;
import org.bitbuckets.lib.log.ILoggable;
import org.bitbuckets.lib.log.ProcessConsole;
import org.bitbuckets.lib.tune.IForceSendTuner;
import org.bitbuckets.lib.tune.IValueTuner;
import org.bitbuckets.lib.tune.NoopsTuner;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SubProcess extends AProcess {

    final ShuffleboardContainer rootLayout;
    final ShuffleboardContainer rootSidebar;

    final IConsole console;
    final IDebuggable debuggable;

    final ShuffleboardContainer log;
    final ShuffleboardContainer tune;

    public SubProcess(Path path, IForceSendTuner<ProcessMode> selfMode, ShuffleboardContainer rootLayout, ShuffleboardContainer rootSidebar, IConsole console, IDebuggable debuggable, ShuffleboardContainer selfLog, ShuffleboardContainer selfTune) {
        super(path, selfMode);
        this.rootLayout = rootLayout;
        this.rootSidebar = rootSidebar;
        this.console = console;
        this.debuggable = debuggable;
        this.log = selfLog;
        this.tune = selfTune;
    }


    @Override
    public <T> T childSetup(String key, ISetup<T> setup) {
        //component specific
        var childPath = this.selfPath.append(key);

        var childComponent = rootLayout.getLayout(childPath.getTail(), BuiltInLayouts.kGrid).withProperties(Map.of("Number of columns", 3, "Number of rows", 1)).withSize(1, 1);
        var childDebugPart = childComponent.getLayout("debug",BuiltInLayouts.kList).withProperties(Map.of("Label Position", "LEFT"));
        var childTune = childComponent.getLayout("tune", BuiltInLayouts.kList).withProperties(Map.of("Label Position", "BOTTOM"));
        var childLog = childComponent.getLayout("log", BuiltInLayouts.kList).withProperties(Map.of("Label Position", "LEFT"));

        IForceSendTuner<ProcessMode> childMode = (IForceSendTuner<ProcessMode>) ITuneAs.ENUM(ProcessMode.class)
                .generate(
                        childPath.getAsFlatTablePath(),
                        rootSidebar,
                        ProcessMode.LOG_COMPETITION,
                        selfMode
                );

        ShuffleDebuggable childDebug = new ShuffleDebuggable(childDebugPart, childMode);
        ProcessConsole childConsole = new ProcessConsole(childMode,childPath);

        AProcess child = new SubProcess(childPath, childMode, rootLayout, rootSidebar, childConsole, childDebug, childLog, childTune);
        childMode.bind(child::forceTo);
        children.add(child);


        var instance = setup.build(child);

        //after setup.build is run... check if the child quantity is >0 and if so only then form the shuffleboard components..

        if (instance instanceof HasLoop) {
            child.registerLogicLoop((HasLoop) instance);
        }

        if (instance instanceof HasLogLoop) {
            child.registerLogLoop((HasLogLoop) instance);
        }

        return instance;
    }

    @Override
    public <T> T siblingSetup(String key, ISetup<T> setup) {
        return null;
    }

    @Override
    public IConsole getAssociatedConsole() {
        return console;
    }

    @Override
    public IDebuggable getDebuggable() {
        return debuggable;
    }

    Set<String> hasSeen = new HashSet<>();

    @Override
    public <T> IValueTuner<T> generateTuner(ITuneAs<T> tuneDataType, String key, T dataWhenNotTuning) {

        return (IValueTuner<T>) new NoopsTuner();

        /*

        if (hasSeen.contains(key)) {
            throw new IllegalStateException("already using key: " + key);
        }
        hasSeen.add(key);

        return tuneDataType.generate(key, tune, dataWhenNotTuning, selfMode);*/
    }

    @Override
    public <T> ILoggable<T> generateLogger(ILogAs<T> logDataType, String key) {

        return (a) -> {};

        //return logDataType.generate(key, log, selfMode);
    }

    @Override
    public void registerLifecycle(HasLifecycle lifecycle) {

    }

    @Override
    public HasLifecycle offerInternalLifecycler() {
        return null;
    }
}
