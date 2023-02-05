package org.bitbuckets.lib.startup;

public class ErrorRecord {

    public final String processName;
    public final String taskName;
    public final Throwable exception;

    public ErrorRecord(String processName, String taskName, Throwable exception) {
        this.processName = processName;
        this.taskName = taskName;
        this.exception = exception;
    }
}
