package org.bitbuckets.lib.log;

import org.bitbuckets.lib.core.ErrorDriver;
import org.bitbuckets.lib.core.LogDriver;

import java.util.function.Consumer;

public class StartupLogger {

    final LogDriver logDriver;
    final int id;
    final String initializedLoggingPath;
    final ErrorDriver errorDriver;

    public StartupLogger(LogDriver logDriver, int id, String initializedLoggingPath, ErrorDriver errorDriver) {
        this.logDriver = logDriver;
        this.id = id;
        this.initializedLoggingPath = initializedLoggingPath;
        this.errorDriver = errorDriver;
    }

    public void signalProcessing() {
        //TODO signal logs
        logDriver.report(id, initializedLoggingPath, "processing");
    }

    public void signalCompleted() {
        //TODO signal logs
        logDriver.report(id, initializedLoggingPath, "completed");
    }

    /**
     * Automatically kills a signal
     * @param error
     */
    public void signalErrored(String error) {

        logDriver.report(id, initializedLoggingPath, "errored");
        logDriver.report(id, initializedLoggingPath + "-report", error);

        errorDriver.flagError(id, error);

        throw new IllegalStateException(error); //cause a crash //TODO error driver
    }

    //matt is planning things...
    public void fullLoop(Consumer<Consumer<String>> aaa) {
        throw new UnsupportedOperationException();
    }

}
