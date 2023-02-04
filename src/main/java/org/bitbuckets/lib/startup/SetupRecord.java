package org.bitbuckets.lib.startup;

public class SetupRecord {

    final String fullpath;
    final String taskName;
    final String message;

    public SetupRecord(String fullpath, String taskName, String message) {
        this.fullpath = fullpath;
        this.taskName = taskName;
        this.message = message;
    }
}
