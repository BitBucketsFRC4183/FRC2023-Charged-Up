package org.bitbuckets.lib.process;

import edu.wpi.first.wpilibj.shuffleboard.BuiltInLayouts;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardContainer;
import org.bitbuckets.lib.*;
import org.bitbuckets.lib.core.Path;
import org.bitbuckets.lib.debug.IDebuggable;
import org.bitbuckets.lib.debug.ShuffleDebuggable;
import org.bitbuckets.lib.log.IConsole;
import org.bitbuckets.lib.log.ILoggable;
import org.bitbuckets.lib.log.ProcessConsole;
import org.bitbuckets.lib.tune.IForceSendTuner;
import org.bitbuckets.lib.tune.IValueTuner;
import org.bitbuckets.lib.tune.NoopsTuner;
import org.bitbuckets.lib.util.HasLogLoop;
import org.bitbuckets.lib.util.HasLoop;

import java.util.Map;

public class SubProcess extends AProcess {

    final ShuffleboardContainer layout;
    final ShuffleboardContainer sidebar;

    final IConsole console;
    final IDebuggable debuggable;

    final ShuffleboardContainer log;
    final ShuffleboardContainer tune;

    public SubProcess(Path path, IForceSendTuner<ProcessMode> selfMode, ShuffleboardContainer layout, ShuffleboardContainer sidebar, IConsole console, IDebuggable debuggable, ShuffleboardContainer log, ShuffleboardContainer tune) {
        super(path, selfMode);
        this.layout = layout;
        this.sidebar = sidebar;
        this.console = console;
        this.debuggable = debuggable;
        this.log = log;
        this.tune = tune;
    }


    @Override
    public <T> T childSetup(String key, ISetup<T> setup) {
        //component specific
        var path = this.path.append(key);

        var component = layout.getLayout(path.getAsLastTwoPathFlat().orElse(key), BuiltInLayouts.kGrid).withProperties(Map.of("Number of columns", 3, "Number of rows", 1)).withSize(3, 2);
        var debug = component.getLayout("debug",BuiltInLayouts.kList).withProperties(Map.of("Label Position", "LEFT"));
        var tune = component.getLayout("tune", BuiltInLayouts.kList).withProperties(Map.of("Label Position", "BOTTOM"));
        var log = component.getLayout("log", BuiltInLayouts.kList).withProperties(Map.of("Label Position", "LEFT"));

        /*IForceSendTuner<ProcessMode> childMode = (IForceSendTuner<ProcessMode>) ITuneAs.SIDEBAR_ENUM(ProcessMode.class)
                .generate(
                        "changer",
                        sidebar,
                        ProcessMode.LOG_COMPETITION,
                        selfMode
                );*/

        ShuffleDebuggable debuggable = new ShuffleDebuggable(debug, this.selfMode);
        ProcessConsole console = new ProcessConsole(selfMode,path);

        AProcess child = new SubProcess(path, new NoopsTuner(), layout, sidebar, console, debuggable, log, tune);
        //childMode.bind(child::forceTo);
        children.add(child);


        var instance = setup.build(child);

        if (instance instanceof HasLoop) {
            child.registerLogicLoop((HasLoop) instance);
        }

        if (instance instanceof HasLogLoop) {
            child.registerLogLoop((HasLogLoop) instance);
        }

        return instance;
    }

    @Override
    public IConsole getAssociatedConsole() {
        return console;
    }

    @Override
    public IDebuggable getDebuggable() {
        return debuggable;
    }

    @Override
    public <T> IValueTuner<T> generateTuner(ITuneAs<T> tuneDataType, String key, T dataWhenNotTuning) {
        return tuneDataType.generate(key, tune, dataWhenNotTuning, selfMode);
    }

    @Override
    public <T> ILoggable<T> generateLogger(ILogAs<T> logDataType, String key) {
        return logDataType.generate(key, log, selfMode);
    }
}
