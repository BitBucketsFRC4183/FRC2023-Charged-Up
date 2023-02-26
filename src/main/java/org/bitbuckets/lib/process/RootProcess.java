package org.bitbuckets.lib.process;

import edu.wpi.first.wpilibj.shuffleboard.BuiltInLayouts;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardContainer;
import org.bitbuckets.lib.*;
import org.bitbuckets.lib.core.Path;
import org.bitbuckets.lib.debug.IDebuggable;
import org.bitbuckets.lib.debug.NoopsDebuggable;
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

public class RootProcess extends AProcess {

    public RootProcess(Path path, IForceSendTuner<ProcessMode> selfMode) {
        super(path, selfMode);
    }

    public static int i = 0;



    @Override
    public <T> T childSetup(String key, ISetup<T> setup) {
        var path = this.selfPath.append(key);

        ShuffleboardContainer tab = Shuffleboard.getTab(key);
        ShuffleboardContainer sidebar = tab.getLayout("enablers", BuiltInLayouts.kList);


        ShuffleboardContainer component = tab.getLayout(key, BuiltInLayouts.kGrid).withProperties(Map.of("Number of columns", 3, "Number of rows", 1)).withSize(3, 2);
        ShuffleboardContainer debug = component.getLayout("debug",BuiltInLayouts.kList).withProperties(Map.of("Label Position", "LEFT"));
        ShuffleboardContainer tune = component.getLayout("tune", BuiltInLayouts.kList).withProperties(Map.of("Label Position", "BOTTOM"));
        ShuffleboardContainer log = component.getLayout("log", BuiltInLayouts.kList).withProperties(Map.of("Label Position", "LEFT"));

        IForceSendTuner<ProcessMode> childMode = (IForceSendTuner<ProcessMode>) ITuneAs.SIDEBAR_ENUM(ProcessMode.class)
                .generate(
                        path.getAsFlatTablePath(),
                        sidebar,
                        ProcessMode.LOG_COMPETITION,
                        selfMode
                );


        IDebuggable debuggable = new NoopsDebuggable();
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

    public static RootProcess root() {


        return new RootProcess(new Path(new String[0]), new NoopsTuner(ProcessMode.LOG_COMPETITION));
    }
 }
