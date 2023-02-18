package org.bitbuckets.lib;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInLayouts;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardContainer;
import org.bitbuckets.lib.core.Path;
import org.bitbuckets.lib.debug.IDebuggable;
import org.bitbuckets.lib.debug.NoopsDebuggable;
import org.bitbuckets.lib.debug.ShuffleDebuggable;
import org.bitbuckets.lib.log.IConsole;
import org.bitbuckets.lib.log.ILoggable;
import org.bitbuckets.lib.log.NoopsConsole;
import org.bitbuckets.lib.log.ProcessConsole;
import org.bitbuckets.lib.tune.EnumTuner;
import org.bitbuckets.lib.tune.IForceSendTuner;
import org.bitbuckets.lib.tune.IValueTuner;
import org.bitbuckets.lib.tune.NoopsTuner;
import org.bitbuckets.lib.util.HasLogLoop;
import org.bitbuckets.lib.util.HasLoop;

import java.util.ArrayList;
import java.util.List;

public class Process implements IProcess {

    final List<Process> children = new ArrayList<>();
    final List<HasLoop> runLogic = new ArrayList<>();
    final List<HasLogLoop> runLogging = new ArrayList<>();

    final ShuffleboardContainer table;
    final Path selfPath;
    final IForceSendTuner<ProcessMode> selfMode;
    final IConsole selfConsole;
    final IDebuggable selfDbg;

    public Process(ShuffleboardContainer table, Path selfPath, IConsole selfConsole, IForceSendTuner<ProcessMode> selfMode, IDebuggable selfDbg) {
        this.table = table;
        this.selfPath = selfPath;
        this.selfConsole = selfConsole;
        this.selfMode = selfMode;
        this.selfDbg = selfDbg;

    }

    @Override
    public Path getSelfPath() {
        return selfPath;
    }

    @Override
    public IConsole getAssociatedConsole() {
        return selfConsole;
    }

    @Override
    public IDebuggable getDebuggable() {
        return selfDbg;
    }

    @Override
    public <T> IValueTuner<T> generateTuner(ITuneAs<T> tuneDataType, String key, T dataWhenNotTuning) {
        return tuneDataType.generate(key, table, dataWhenNotTuning, selfMode, null);
    }


    @Override
    public <T> ILoggable<T> generateLogger(ILogAs<T> logDataType, String key) {
        return logDataType.generate(key,table);
    }

    @Override
    public void registerLogLoop(HasLogLoop loop) {
        runLogging.add(loop);
    }

    @Override
    public void registerLogicLoop(HasLoop loop) {
        runLogic.add(loop);
    }

    @Override
    public void run() {
        int currentLevel = selfMode.readValue().level;
        //if process is disabled, do nothing
        if (currentLevel > ProcessMode.DISABLED.level) return;


        //run children first
        for (Process process : children) { //TODO profile children
            process.run();
        }

        //run own logic loops
        for (HasLoop hasLoop : runLogic) {
            hasLoop.loop();
        }

        //If allowed to, run logging loop
        if (currentLevel <= ProcessMode.LOG_PROFILING.level) {
            for (HasLogLoop hasLogLoop : runLogging) {
                hasLogLoop.logLoop();
            }
        }

    }

    public void forceTo(ProcessMode mode) {
        for (Process process : children) {
            process.selfConsole.sendInfo("changed mode to: " + mode.name());
            process.selfMode.forceToValue(mode);

            process.forceTo(mode);
        }
    }


    @Override
    public <T> T childSetup(String key, ISetup<T> setup) {
        Path childPath = selfPath.append(key);
        ShuffleboardContainer childContainer;
        if (childPath.length() == 1) { //if this is directly branching off the root, it should go in a tab
            childContainer = Shuffleboard.getTab(key);
        } else {
            //base this off relation
            childContainer = table.getLayout(key, BuiltInLayouts.kGrid); //TODO make nesting look better, don't stack 1 billion components inside eachother
        }

        //setup tuner that can change values internally
        EnumTuner<ProcessMode> childModeTuner = new EnumTuner<>(
                childContainer,
                ProcessMode.class,
                ProcessMode.LOG_COMPETITION,
                e -> forceTo(ProcessMode.valueOf(e.valueData.value.getString()))
        );



        //setup console for writing data to
        IConsole childConsole = new ProcessConsole(childModeTuner, childPath);
        IDebuggable childDebuggable = new ShuffleDebuggable(childContainer, childModeTuner);

        Process child = new Process(childContainer, childPath, childConsole, childModeTuner, childDebuggable);
        children.add(child);

        var p = setup.build(child);

        if (p instanceof HasLoop) {
            child.registerLogicLoop((HasLoop) p);
        }

        if (p instanceof HasLogLoop) {
            child.registerLogLoop((HasLogLoop) p);
        }

        return p;
    }

    @Override
    public <T> T inlineSetup(ISetup<T> setup) {
        return setup.build(this);
    }


    public static Process root() {
        //TODO

        return new Process(
                null,
                new Path(new String[0]),
                new NoopsConsole(),
                new NoopsTuner(),
                new NoopsDebuggable());
    }
}
