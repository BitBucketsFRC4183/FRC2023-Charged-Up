package org.bitbuckets.lib.process;

import edu.wpi.first.wpilibj.shuffleboard.BuiltInLayouts;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import org.bitbuckets.auto.AutoFSM;
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

import java.util.Map;

public class RootProcess extends AProcess implements HasLifecycle {

    public RootProcess(Path path, IForceSendTuner<ProcessMode> selfMode) {
        super(path, selfMode);
    }



    @Override
    public <T> T childSetup(String key, ISetup<T> setup) {

        var path = this.selfPath.append(key);

        var tab = Shuffleboard.getTab(key);
        var sidebar = tab.getLayout("enablers", BuiltInLayouts.kList);

        //component specific
        var component = tab.getLayout(key, BuiltInLayouts.kGrid).withProperties(Map.of("Number of columns", 3, "Number of rows", 1)).withSize(3, 2);
        var debug = component.getLayout("debug",BuiltInLayouts.kList).withProperties(Map.of("Label Position", "LEFT"));
        var tune = component.getLayout("tune", BuiltInLayouts.kList).withProperties(Map.of("Label Position", "BOTTOM"));
        var log = component.getLayout("log", BuiltInLayouts.kList).withProperties(Map.of("Label Position", "LEFT"));

        IForceSendTuner<ProcessMode> childMode = (IForceSendTuner<ProcessMode>) ITuneAs.ENUM(ProcessMode.class)
                .generate(
                        path.getAsFlatTablePath(),
                        sidebar,
                        ProcessMode.LOG_COMPETITION,
                        selfMode
                );


        ShuffleDebuggable debuggable = new ShuffleDebuggable(debug, childMode);
        ProcessConsole console = new ProcessConsole(childMode,path);

        AProcess child = new SubProcess(path, childMode, tab, sidebar, console, debuggable, log, tune);
        childMode.bind(child::forceTo);
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
    public <T> T siblingSetup(String key, ISetup<T> setup) {
        throw new IllegalStateException("Illegal root sibling at: " + key);
    }


    @Override
    public IConsole getAssociatedConsole() {
        throw new UnsupportedOperationException("cannot use console");
    }

    @Override
    public IDebuggable getDebuggable() {
        throw new UnsupportedOperationException("cannot use debug");
    }

    @Override
    public <T> IValueTuner<T> generateTuner(ITuneAs<T> tuneDataType, String key, T dataWhenNotTuning) {
        throw new UnsupportedOperationException("cannot use tuner");
    }

    @Override
    public <T> ILoggable<T> generateLogger(ILogAs<T> logDataType, String key) {
        throw new UnsupportedOperationException("cannot use logger");
    }

    @Override
    public void registerLifecycle(HasLifecycle lifecycle) {

    }

    @Override
    public HasLifecycle offerInternalLifecycler() {
        return null;
    }

    public static RootProcess root() {


        return new RootProcess(new Path(new String[0]), new NoopsTuner());
    }

    @Override
    public void onEvent(String autoEvent) {

    }

    @Override
    public void onPhaseChangeEvent(AutoFSM phase) {

    }
}
