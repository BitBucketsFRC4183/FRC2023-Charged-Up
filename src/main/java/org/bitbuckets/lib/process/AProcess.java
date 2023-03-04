package org.bitbuckets.lib.process;

import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardContainer;
import org.bitbuckets.lib.IDoWhenReady;
import org.bitbuckets.lib.IProcess;
import org.bitbuckets.lib.ProcessMode;
import org.bitbuckets.lib.core.HasLogLoop;
import org.bitbuckets.lib.core.HasLoop;
import org.bitbuckets.lib.core.Path;
import org.bitbuckets.lib.tune.IForceSendTuner;

import java.util.ArrayList;
import java.util.List;

public abstract class AProcess implements IProcess, IDoWhenReady {

    final Path selfPath;
    IForceSendTuner<ProcessMode> selfMode;

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

    protected abstract void internalReady(ShuffleboardContainer root, ShuffleboardContainer enablers);


}
