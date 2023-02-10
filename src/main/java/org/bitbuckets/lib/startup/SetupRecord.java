package org.bitbuckets.lib.startup;

public class SetupRecord {

    final int taskId;
    final String processPath;
    final String taskName;
    final String message;

    public SetupRecord(int taskId, String processPath, String taskName, String message) {
        this.taskId = taskId;
        this.processPath = processPath;
        this.taskName = taskName;
        this.message = message;
    }
}
