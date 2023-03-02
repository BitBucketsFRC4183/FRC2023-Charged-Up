package org.bitbuckets.lib.process;

import edu.wpi.first.networktables.GenericEntry;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInLayouts;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardContainer;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardLayout;
import org.bitbuckets.lib.*;
import org.bitbuckets.lib.core.HasLifecycle;
import org.bitbuckets.lib.core.Path;
import org.bitbuckets.lib.debug.IDebuggable;
import org.bitbuckets.lib.debug.NoopsDebuggable;
import org.bitbuckets.lib.debug.ShuffleDebuggable;
import org.bitbuckets.lib.log.IConsole;
import org.bitbuckets.lib.log.ILoggable;
import org.bitbuckets.lib.tune.IForceSendTuner;
import org.bitbuckets.lib.tune.IValueTuner;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;

public class OnReadyProcess extends AProcess implements IDoWhenReady {


    public OnReadyProcess(Path selfPath, IForceSendTuner<ProcessMode> selfMode) {
        super(selfPath, selfMode);
    }

    Set<RegisterType> has() {
        Set<RegisterType> seen = new HashSet<>();

        for (RegisterRecord record : this.onReady) {

            if (record.type == RegisterType.SIDEBAR) continue;
            seen.add(record.type);
        }

        return seen;
    }

    Set<RegisterRecord> sort(RegisterType type) {
        Set<RegisterRecord> records = new HashSet<>();

        for (RegisterRecord record : this.onReady) {
            if (record.type == type) {
                records.add(record);
            }
        }
        return records;
    }

    //called later
    @Override
    protected void internalReady(ShuffleboardContainer root, ShuffleboardContainer enablers) {

        if (!has().isEmpty()) {
            Set<RegisterType> has = has();

            ShuffleboardContainer component = root.getLayout(
                            selfPath
                                    .getAsLastTwoPathFlat()
                                    .orElse(selfPath.getTail()),
                            BuiltInLayouts.kGrid
                    ).withProperties(Map.of("Number of columns", has.size(), "Number of rows", 1))
                    .withSize(has.size(), 1);


            Set<RegisterRecord> debugs = sort(RegisterType.DEBUG);
            Set<RegisterRecord> logs = sort(RegisterType.LOG);
            Set<RegisterRecord> tunes = sort(RegisterType.TUNE);
            Set<RegisterRecord> sidebar = sort(RegisterType.SIDEBAR);

            if (debugs.size() > 0) {
                ShuffleboardContainer debugComponent = component
                        .getLayout("debug",BuiltInLayouts.kList)
                        .withProperties(Map.of("Label Position", "LEFT"));

                for (RegisterRecord record : debugs) {
                    var entry = record.fn.apply(debugComponent);
                    record.ftr.complete(entry);
                }
            }

            if (logs.size() > 0) {
                ShuffleboardContainer debugComponent = component
                        .getLayout("log",BuiltInLayouts.kList)
                        .withProperties(Map.of("Label Position", "LEFT"));

                for (RegisterRecord record : logs) {
                    var entry = record.fn.apply(debugComponent);
                    record.ftr.complete(entry);
                }
            }

            if (tunes.size() > 0) {
                ShuffleboardContainer debugComponent = component
                        .getLayout("tune",BuiltInLayouts.kList)
                        .withProperties(Map.of("Label Position", "LEFT"));

                for (RegisterRecord record : tunes) {
                    var entry = record.fn.apply(debugComponent);
                    record.ftr.complete(entry);
                }
            }

            if (sidebar.size() > 0) {
                for (RegisterRecord record : sidebar) {
                    var entry = record.fn.apply(enablers);

                    record.ftr.complete(entry);
                }
            }
        }

        for (AProcess child : this.children) {
            child.internalReady(root, enablers);
        }


    }

    record RegisterRecord(Function<ShuffleboardContainer, GenericEntry> fn, CompletableFuture<GenericEntry> ftr, org.bitbuckets.lib.process.RegisterType type) {}
    final List<RegisterRecord> onReady = new ArrayList<>();



    @Override
    public CompletableFuture<GenericEntry> doWhenReady(Function<ShuffleboardContainer, GenericEntry> fn, RegisterType type) {
        CompletableFuture<GenericEntry> empty = new CompletableFuture<>();

        onReady.add(new RegisterRecord(fn, empty, type));

        return empty;
    }

    @Override
    public CompletableFuture<Void> doWhenReady(Consumer<ShuffleboardContainer> container, RegisterType type) {
        CompletableFuture<GenericEntry> empty = new CompletableFuture<>();

        onReady.add(new RegisterRecord(a -> { container.accept(a); return null; }, empty, type));

        return empty.thenAccept(ignore -> {});
    }

    @Override
    public CompletableFuture<ShuffleboardContainer> doWhenReadyDbg() {
        CompletableFuture<ShuffleboardContainer> empty = new CompletableFuture<>();

        onReady.add(new RegisterRecord(a -> {empty.complete(a); return null;}, new CompletableFuture<>(), RegisterType.DEBUG));

        return empty;
    }

    @Override
    public <T> T childSetup(String key, ISetup<T> setup) {
        Path childPath = selfPath.append(key);

        IForceSendTuner<ProcessMode> childMode = (IForceSendTuner<ProcessMode>) ITuneAs.SIDE_ENUM(ProcessMode.class)
                .generate(
                        childPath.getAsFlatTablePath(),
                        this,
                        ProcessMode.LOG_COMPETITION,
                        selfMode
                );

        OnReadyProcess child = new OnReadyProcess(childPath, childMode);
        childMode.bind(child::forceTo);
        this.children.add(child);

        return setup.build(child);
    }

    @Override
    public <T> T siblingSetup(String key, ISetup<T> setup) {
        throw new UnsupportedOperationException();
    }

    @Override
    public IConsole getAssociatedConsole() {
        throw new UnsupportedOperationException();
    }


    ShuffleDebuggable cached = null;
    @Override
    public IDebuggable getDebuggable() {


        if (cached == null) {

            CompletableFuture<ShuffleboardContainer> ctr = doWhenReadyDbg();

            cached = new ShuffleDebuggable(ctr, selfMode);
        }

        return cached;

    }

    @Override
    public <T> IValueTuner<T> generateTuner(ITuneAs<T> tuneDataType, String key, T dataWhenNotTuning) {
        return tuneDataType.generate(key, this, dataWhenNotTuning, selfMode);
    }

    @Override
    public <T> ILoggable<T> generateLogger(ILogAs<T> logDataType, String key) {
        return logDataType.generate(key, this, selfMode);
    }

    @Override
    public void registerLifecycle(HasLifecycle lifecycle) {
        throw new UnsupportedOperationException();
    }

    @Override
    public HasLifecycle offerInternalLifecycler() {
        throw new UnsupportedOperationException();
    }
}
