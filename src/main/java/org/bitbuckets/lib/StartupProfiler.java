package org.bitbuckets.lib;

import org.bitbuckets.lib.startup.IStartupDriver;

public class StartupProfiler {

    final IStartupDriver startupDriver;
    final int taskId;

    StartupProfiler(IStartupDriver startupDriver, int taskId) {
        this.startupDriver = startupDriver;
        this.taskId = taskId;
    }

    public void markProcessing() {
        startupDriver.reportStartupProcessing(taskId);
    }

    public void markCompleted() {
        startupDriver.reportCompleted(taskId);
    }

    public void markErrored(Throwable throwable) {
        startupDriver.reportStartupError(taskId, throwable);
    }

    public void sendInfo(String info) {
        startupDriver.reportStartupInfo(taskId, info);
    }


}
