package org.bitbuckets.lib;

import org.bitbuckets.lib.startup.SetupDriver;

@Deprecated @DontUseIncubating
public class SetupProfiler {

    final SetupDriver startupDriver;
    final int taskId;

    SetupProfiler(SetupDriver startupDriver, int taskId) {
        this.startupDriver = startupDriver;
        this.taskId = taskId;
    }

    public void markProcessing() {
        startupDriver.reportStartupProcessing(taskId);
    }

    public void markCompleted() {
        startupDriver.reportCompleted(taskId);
    }

    /**
     * Automatically kills a signal
     *
     * @param error
     */
    public void markErrored(String error) {
        startupDriver.reportStartupError(taskId, error);

    }

    public void sendInfo(String info) {
        startupDriver.reportStartupInfo(taskId, info);
    }

    //matt is planning things...

}
