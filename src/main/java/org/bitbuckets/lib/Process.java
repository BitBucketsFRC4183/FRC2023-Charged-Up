package org.bitbuckets.lib;

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
import org.bitbuckets.lib.tune.IForceSendTuner;
import org.bitbuckets.lib.tune.IValueTuner;
import org.bitbuckets.lib.tune.NoopsTuner;
import org.bitbuckets.lib.util.HasLogLoop;
import org.bitbuckets.lib.util.HasLoop;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class Process implements IProcess {

    final List<Process> children = new ArrayList<>();
    final List<HasLoop> runLogic = new ArrayList<>();
    final List<HasLogLoop> runLogging = new ArrayList<>();

    final ShuffleboardContainer table;
    final Path selfPath;
    final IForceSendTuner<ProcessMode> selfMode;
    final IConsole selfConsole;
    final IDebuggable selfDbg;

    final ShuffleboardContainer log; //listmode
    final ShuffleboardContainer dbg;
    final ShuffleboardContainer tune;

    public Process(ShuffleboardContainer table, Path selfPath, IConsole selfConsole, IForceSendTuner<ProcessMode> selfMode, IDebuggable selfDbg) {
        this.table = table;
        this.selfPath = selfPath;
        this.selfConsole = selfConsole;
        this.selfMode = selfMode;
        this.selfDbg = selfDbg;

        if (table != null) {
            log = table.getLayout("loggables", BuiltInLayouts.kList);
            dbg = table.getLayout("debuggables", BuiltInLayouts.kList);
            tune = table.getLayout("tuneables", BuiltInLayouts.kList);
        } else {
            log = Shuffleboard.getTab("ERROR");
            dbg = Shuffleboard.getTab("ERROR");
            tune = Shuffleboard.getTab("ERROR");
        }



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
        return tuneDataType.generate(key, tune, dataWhenNotTuning, selfMode);
    }


    @Override
    public <T> ILoggable<T> generateLogger(ILogAs<T> logDataType, String key) {
        return logDataType.generate(key,log, selfMode);
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

    @Override
    public int componentQuantity() {
        int count = children.size();

        for (Process process : children) {
            count += process.componentQuantity();
        }


        return count;
    }

    public synchronized void forceTo(ProcessMode mode) {
        for (Process process : children) {
            System.out.println("changed "+ mode.name());
            process.selfConsole.sendInfo("changed mode to: " + mode.name());
            process.selfMode.forceToValue(mode);

            process.forceTo(mode);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T childSetup(String key, ISetup<T> setup)  {
        System.out.println(this.children.size());

        var childPath = selfPath.append(key);
        //todo less sloppy
        ShuffleboardContainer childContainer;
        if (childPath.length() == 1) { //if this is directly branching off the root, it should go in a tab
            childContainer = Shuffleboard.getTab(key);
        } else if (childPath.length() == 2) {

            childContainer = table.getLayout(key, BuiltInLayouts.kGrid).withSize(4, 2);//.withProperties(Map.of("number of columns", width, "number of rows", length));


        } else {
            childContainer = table.getLayout(key, BuiltInLayouts.kList);


        }

        IForceSendTuner<ProcessMode> childMode = (IForceSendTuner<ProcessMode>) ITuneAs.ENUM_INPUT(ProcessMode.class)
                .generate(
                        "changer",
                        childContainer,
                        ProcessMode.LOG_COMPETITION,
                        selfMode
                );


        LazyConsumer<ProcessMode> weakRefConsumer = new LazyConsumer<>();
        childMode.bind(weakRefConsumer);

        //setup console for writing data to
        IConsole childConsole = new ProcessConsole(childMode, childPath);
        IDebuggable childDebuggable = new ShuffleDebuggable(childContainer, childMode);

        Process child = new Process(childContainer, childPath, childConsole, childMode, childDebuggable);
        children.add(child);

        weakRefConsumer.late(child::forceTo);

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

        var tuner = (IForceSendTuner<ProcessMode>) ITuneAs.ENUM_INPUT(ProcessMode.class)
                .generate(
                        "changer",
                        Shuffleboard.getTab("mattlib"),
                        ProcessMode.LOG_COMPETITION,
                        null
                );

        return new Process(
                null,
                new Path(new String[0]),
                new NoopsConsole(),
                tuner,
                new NoopsDebuggable());
    }
}
