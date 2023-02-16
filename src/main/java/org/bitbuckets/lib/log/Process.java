package org.bitbuckets.lib.log;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import org.bitbuckets.lib.*;
import org.bitbuckets.lib.core.Path;
import org.bitbuckets.lib.tune.EnumTuner;
import org.bitbuckets.lib.tune.IForceSendTuner;
import org.bitbuckets.lib.tune.IValueTuner;
import org.bitbuckets.lib.tune.ModernTuner;

import java.util.ArrayList;
import java.util.List;

public class Process implements IProcess {

    final List<Process> children = new ArrayList<>();

    final List<CanLoop> runLogic = new ArrayList<>();
    final List<CanLogLoop> runLogging = new ArrayList<>();

    final NetworkTable table;
    final Path selfPath;
    final IForceSendTuner<ProcessMode> selfMode;
    final IConsole selfConsole;
    final IDebuggable selfDbg;

    public Process(NetworkTable table, Path selfPath, IConsole selfConsole, IForceSendTuner<ProcessMode> selfMode, IDebuggable selfDbg) {
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

    <T extends Enum<T>, A> IValueTuner<T> gen(NetworkTable sub, Class<A> tuneDT, A dtNotTune) {
        return new EnumTuner<>(sub, (Class<T>) tuneDT, (T) dtNotTune, a -> {});
    }

    @Override
    public <T> IValueTuner<T> generateTuner(Class<T> tuneDataType, String key, T dataWhenNotTuning) {
        if (tuneDataType == Enum.class) {
            NetworkTable sub = table.getSubTable("tune-" + key);

            IValueTuner<?> s =  gen(sub, tuneDataType, dataWhenNotTuning);
            return (IValueTuner<T>) s;
        } else {
            NetworkTableEntry entry = table.getEntry("tune-" + key);

            return new ModernTuner<>(entry, selfMode, dataWhenNotTuning);
        }
    }

    @Override
    public <T> ILoggable<T> generateLogger(Class<T> logDataType, String key) {
        NetworkTableEntry sub = table.getEntry("log-" + key);
        ILoggable<T> log = null;

        if (logDataType == Double.class) {
            log = a -> sub.setDouble((Double) a);
        }

        if (logDataType == Boolean.class) {
            log = a -> sub.setBoolean((Boolean) a);
        }

        if (logDataType == String.class) {
            log = a -> sub.setString((String) a);
        }

        if (log != null) {
            return log;
        }

        throw new UnsupportedOperationException("bad data type");
    }

    @Override
    public void registerLogLoop(CanLogLoop loop) {
        runLogging.add(loop);
    }

    @Override
    public void registerLogicLoop(CanLoop loop) {
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

        for (CanLoop canLoop : runLogic) {
            canLoop.loop();
        }

        //If allowed to, run logging loop

        if (currentLevel > ProcessMode.LOG_PROFILING.level) {
            for (CanLogLoop canLogLoop : runLogging) {
                canLogLoop.logLoop();
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
    public IProcess child(String key) {

        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T childSetup(String key, ISetup<T> setup) {
        Path childPath = selfPath.append(key);
        NetworkTable childTable = table.getSubTable(key);

        NetworkTable childMode = childTable.getInstance().getTable("mattlib").getSubTable(childPath.getAsFlatTablePath() + "-set-mode");

        //setup metadata in ShuffleBoard


        //setup tuner that can change values internally
        EnumTuner<ProcessMode> childModeTuner = new EnumTuner<>(
                childMode,
                ProcessMode.class,
                ProcessMode.LOG_COMPETITION,
                e -> forceTo(ProcessMode.valueOf(e.valueData.value.getString()))
        );

        //setup console for writing data to
        IConsole childConsole = new ProcessConsole(childModeTuner, childPath);
        IDebuggable childDebuggable = new NetworkDebuggable(childTable, childModeTuner);

        Process child = new Process(childTable, childPath, childConsole, childModeTuner, childDebuggable);
        children.add(child);

        //TODO automatically register any hasLoops on child...

        return setup.build(child);
    }

    @Override
    public <T> T inlineSetup(ISetup<T> setup) {
        return setup.build(this);
    }

    public static Process root() {
        throw new UnsupportedOperationException();
    }
}
