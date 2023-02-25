package org.bitbuckets.lib.process;

import org.bitbuckets.lib.*;
import org.bitbuckets.lib.core.Path;
import org.bitbuckets.lib.tune.IForceSendTuner;
import org.bitbuckets.lib.util.HasLogLoop;
import org.bitbuckets.lib.util.HasLoop;

import java.util.ArrayList;
import java.util.List;

public abstract class AProcess implements IProcess {

    final Path selfPath;
    final IForceSendTuner<ProcessMode> selfMode;

    final List<AProcess> children = new ArrayList<>();
    final List<HasLoop> hasLoop = new ArrayList<>();
    final List<HasLogLoop> hasLogLoop = new ArrayList<>();

    protected AProcess(Path selfPath, IForceSendTuner<ProcessMode> selfMode) {
        this.selfPath = selfPath;
        this.selfMode = selfMode;
    }

    @Override
    public Path getSelfPath() {
        return selfPath;
    }

    @Override
    public void forceTo(ProcessMode mode) {
        selfMode.forceToValue(mode);

        for (AProcess aProcess : children) {
            aProcess.forceTo(mode);
        }
    }

    @Override
    public void registerLogLoop(HasLogLoop loop) {
        hasLogLoop.add(loop);
    }

    @Override
    public void registerLogicLoop(HasLoop loop) {
        hasLoop.add(loop);
    }

    @Override
    public void run() {
        //run children first
        for (IProcess process : children) {
            process.run();
        }

        int currentLevel = selfMode.readValue().level;
        //if process is disabled, do nothing
        if (currentLevel > ProcessMode.DISABLED.level) return;

        //run own logic loops
        for (HasLoop hasLoop : this.hasLoop) {
            hasLoop.loop();
        }

        //If allowed to, run logging loop
        if (currentLevel <= ProcessMode.LOG_PROFILING.level) {
            for (HasLogLoop hasLogLoop : this.hasLogLoop) {
                hasLogLoop.logLoop();
            }
        }
    }

    @Override
    public int componentQuantity() {
        int count = children.size();

        for (AProcess process : children) {
            count += process.componentQuantity();
        }


        return count;
    }
}
