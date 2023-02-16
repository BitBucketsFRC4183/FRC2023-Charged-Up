package org.bitbuckets.lib;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import org.bitbuckets.lib.core.Path;
import org.bitbuckets.lib.log.*;
import org.bitbuckets.lib.tune.EnumTuner;
import org.bitbuckets.lib.tune.IForceSendTuner;
import org.bitbuckets.lib.tune.IValueTuner;
import org.bitbuckets.lib.tune.ModernTuner;

import java.util.ArrayList;
import java.util.List;

public class Process implements IProcess {

    final List<Process> children = new ArrayList<>();

    final List<HasLoop> runLogic = new ArrayList<>();
    final List<HasLogLoop> runLogging = new ArrayList<>();

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
            sub.setDouble(0.0);
        }

        if (logDataType == Boolean.class) {
            log = a -> sub.setBoolean((Boolean) a);
            sub.setBoolean(false);
        }

        if (logDataType == String.class) {
            log = a -> sub.setString((String) a);
            sub.setString("default string");
        }

        if (logDataType.isEnum()) {
            log = a -> sub.setString(a.toString());
            sub.setString("default enum");
        }

        if (log != null) {
            return log;
        }

        throw new UnsupportedOperationException("bad data type");
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
                System.out.println("AAAAA");
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
        NetworkTable childMode = childTable.getInstance().getTable("mattlib/").getSubTable( childPath.getAsTablePath() + "mode");

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

    public static Process root(NetworkTable root) {
        NetworkTable childMode = NetworkTableInstance.getDefault().getTable("mattlib").getSubTable("root" + "-set-mode");

        return new Process(
                root,
                new Path(new String[0]),
                new NoopsConsole(),
                new EnumTuner<>(childMode, ProcessMode.class, ProcessMode.LOG_COMPETITION, a-> {}),
                new NoopsDebuggable());
    }
}
