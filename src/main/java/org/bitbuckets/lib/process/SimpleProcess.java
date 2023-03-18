package org.bitbuckets.lib.process;

import org.bitbuckets.lib.ILogAs;
import org.bitbuckets.lib.IProcess;
import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.ITuneAs;
import org.bitbuckets.lib.core.HasLifecycle;
import org.bitbuckets.lib.core.HasLogLoop;
import org.bitbuckets.lib.core.HasLoop;
import org.bitbuckets.lib.core.Path;
import org.bitbuckets.lib.log.IDebuggable;
import org.bitbuckets.lib.log.ILoggable;
import org.bitbuckets.lib.log.SimpleDebuggable;
import org.bitbuckets.lib.tune.IValueTuner;

import java.util.ArrayList;
import java.util.List;

public class SimpleProcess implements IProcess {

    final boolean isReal;
    final Path selfPath;

    public SimpleProcess(boolean isReal, Path selfPath) {
        this.isReal = isReal;
        this.selfPath = selfPath;
    }

    @Override
    public boolean isReal() {
        return isReal;
    }

    @Override
    public Path getSelfPath() {
        return selfPath;
    }

    final List<IProcess> processes = new ArrayList<>();

    <T> T setup(String key, ISetup<T> setup) {
        SimpleProcess child = new SimpleProcess(isReal, selfPath.append(key));
        processes.add(child);
        T toReg = setup.build(child);

        if (toReg instanceof HasLoop) {
            this.loops.add((HasLoop) toReg);
        }

        if (toReg instanceof HasLogLoop) {
            this.logLoops.add((HasLogLoop) toReg);
        }

        if (toReg instanceof HasLifecycle) {
            this.lifecycles.add((HasLifecycle) toReg);
        }

        return toReg;
    }

    @Override
    public <T> T childSetup(String key, ISetup<T> setup) {
        return setup(key, setup);
    }

    @Override
    public <T> T siblingSetup(String key, ISetup<T> setup) {
        return setup(key, setup);
    }

    @Override
    public IDebuggable getDebuggable() {
        return new SimpleDebuggable(this);
    }

    @Override
    public <T> IValueTuner<T> generateTuner(ITuneAs<T> tuneDataType, String key, T dataWhenNotTuning) {
        return tuneDataType.generate(key, selfPath, dataWhenNotTuning);
    }

    @Override
    public <T> ILoggable<T> generateLogger(ILogAs<T> logDataType, String key) {
        return logDataType.generate(key, selfPath);
    }


    final List<HasLogLoop> logLoops = new ArrayList<>();

    @Override
    public void registerLogLoop(HasLogLoop loop) {
        logLoops.add(loop);
    }

    final List<HasLoop> loops = new ArrayList<>();

    @Override
    public void registerLogicLoop(HasLoop loop) {
        loops.add(loop);
    }

    final List<HasLifecycle> lifecycles = new ArrayList<>();

    @Override
    public void registerLifecycle(HasLifecycle lifecycle) {
        lifecycles.add(lifecycle);
    }

    @Override
    public void logLoop() {
        for (IProcess process : processes) {
            process.logLoop();
        }

        for (HasLogLoop logLoop : logLoops) {
            logLoop.logLoop();
        }
    }

    @Override
    public void loop() {
        for (IProcess process : processes) {
            process.loop();
        }

        for (HasLoop loop : loops) {
            loop.loop();
        }
    }
}
