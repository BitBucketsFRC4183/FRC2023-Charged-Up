package org.bitbuckets.lib.process;

import org.bitbuckets.lib.*;
import org.bitbuckets.lib.core.HasLifecycle;
import org.bitbuckets.lib.core.HasLogLoop;
import org.bitbuckets.lib.core.HasLoop;
import org.bitbuckets.lib.core.Path;
import org.bitbuckets.lib.debug.IDebuggable;
import org.bitbuckets.lib.debug.NoopsDebuggable;
import org.bitbuckets.lib.log.IConsole;
import org.bitbuckets.lib.log.ILoggable;
import org.bitbuckets.lib.tune.IValueTuner;
import org.bitbuckets.lib.tune.NoopsTuner;
import org.bitbuckets.lib.tune.PNoopsTuner;

import java.util.ArrayList;
import java.util.List;

public class ForceKillProcess implements IProcess {

    final List<HasLoop> loops = new ArrayList<>();
    final List<IProcess> children = new ArrayList<>();

    @Override
    public Path getSelfPath() {
        return new Path(new String[0]);
    }

    @Override
    public <T> T childSetup(String key, ISetup<T> setup) {

        var child = new ForceKillProcess();
        children.add(child);

        return setup.build(child );
    }

    @Override
    public <T> T siblingSetup(String key, ISetup<T> setup) {
        var child = new ForceKillProcess();
        children.add(child);

        return setup.build(child );
    }

    @Override
    public IConsole getAssociatedConsole() {
        throw new UnsupportedOperationException("no console");
    }

    @Override
    public IDebuggable getDebuggable() {
        return new NoopsDebuggable();
    }

    @Override
    public <T> IValueTuner<T> generateTuner(ITuneAs<T> tuneDataType, String key, T dataWhenNotTuning) {
        return new NoopsTuner<>(dataWhenNotTuning);
    }

    @Override
    public <T> ILoggable<T> generateLogger(ILogAs<T> logDataType, String key) {
        return a -> {}; //ignore
    }

    @Override
    public void forceTo(ProcessMode mode) {
        //ignore
    }

    @Override
    public void registerLogLoop(HasLogLoop loop) {

    }

    @Override
    public void registerLogicLoop(HasLoop loop) {
        loops.add(loop);
    }

    @Override
    public void registerLifecycle(HasLifecycle lifecycle) {

    }

    @Override
    public void run() {
        this.children.forEach(IProcess::run);
    }

    @Override
    public void ready() {

    }

    @Override
    public HasLifecycle offerInternalLifecycler() {
        throw new UnsupportedOperationException();
    }
}
