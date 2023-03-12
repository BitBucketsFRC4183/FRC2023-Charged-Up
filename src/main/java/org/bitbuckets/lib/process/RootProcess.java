package org.bitbuckets.lib.process;

import config.Mattlib;
import edu.wpi.first.networktables.GenericEntry;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInLayouts;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardContainer;
import org.bitbuckets.lib.*;
import org.bitbuckets.lib.core.HasLogLoop;
import org.bitbuckets.lib.core.HasLoop;
import org.bitbuckets.lib.core.Path;
import org.bitbuckets.lib.debug.IDebuggable;
import org.bitbuckets.lib.log.ILoggable;
import org.bitbuckets.lib.tune.IForceSendTuner;
import org.bitbuckets.lib.tune.IValueTuner;
import org.bitbuckets.lib.tune.NoopsTuner;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;


public class RootProcess extends AProcess {

    public RootProcess(Path path, IForceSendTuner<ProcessMode> selfMode) {
        super(path, selfMode);
    }

    final List<ThisRecord> records = new ArrayList<>();


    public void ready() {
        for (ThisRecord thisRecord : records) {

            ShuffleboardContainer tab = Shuffleboard.getTab(thisRecord.name);
            ShuffleboardContainer sidebar = tab.getLayout("enablers", BuiltInLayouts.kList).withSize(1,1);

            thisRecord.self().internalReady(tab, sidebar);
        }
    }

    @Override
    protected void internalReady(ShuffleboardContainer root, ShuffleboardContainer enablers) {
        throw new UnsupportedOperationException();
    }

    record ThisRecord(String name, AProcess self) {}

    @Override
    public <T> T childSetup(String key, ISetup<T> setup) {

        var path = this.selfPath.append(key);

        //TODO this is a terrible hack (setting to null then assigning late)
        AProcess child = new ChildProcess(path, null);

        IForceSendTuner<ProcessMode> childMode = (IForceSendTuner<ProcessMode>) ITuneAs.SIDE_ENUM(ProcessMode.class)
                .generate(
                        path.getAsFlatTablePath(),
                        child,
                        Mattlib.DEFAULT_MODE,
                        selfMode
                );

        child.selfMode = childMode;
        records.add(new ThisRecord(key, child));
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


        return new RootProcess(new Path(new String[0]), new NoopsTuner<>(Mattlib.DEFAULT_MODE));
    }

    @Override
    public CompletableFuture<GenericEntry> doWhenReady(Function<ShuffleboardContainer, GenericEntry> fn, RegisterType type) {
        return new CompletableFuture<>();
    }

    @Override
    public CompletableFuture<Void> doWhenReady(Consumer<ShuffleboardContainer> container, RegisterType type) {
        return new CompletableFuture<>();
    }

    @Override
    public CompletableFuture<ShuffleboardContainer> doWhenReadyDbg() {
        throw new UnsupportedOperationException("what");
    }
}
