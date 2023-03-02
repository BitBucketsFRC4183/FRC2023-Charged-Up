package org.bitbuckets.lib.process;

import edu.wpi.first.networktables.GenericEntry;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInLayouts;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardContainer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
            ShuffleboardContainer sidebar = tab.getLayout("enablers", BuiltInLayouts.kList).withSize(1,5);

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


        IForceSendTuner<ProcessMode> childMode = (IForceSendTuner<ProcessMode>) ITuneAs.ENUM(ProcessMode.class)
                .generate(
                        path.getAsFlatTablePath(),
                        this,
                        ProcessMode.LOG_COMPETITION,
                        selfMode
                );


        AProcess child = new OnReadyProcess(path, childMode);
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
    public CompletableFuture<GenericEntry> doWhenReady(Function<ShuffleboardContainer, GenericEntry> fn, RegisterType type) {
        return new CompletableFuture<>();
    }

    @Override
    public CompletableFuture<Void> doWhenReady(Consumer<ShuffleboardContainer> container, RegisterType type) {
        return new CompletableFuture<>();
    }
}
