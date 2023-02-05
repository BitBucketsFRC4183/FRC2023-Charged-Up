package org.bitbuckets.lib.startup;

public class SetupRecord {

    final int id;
    final String fullpath;
    final String taskName;
    final String message;

    public SetupRecord(int id, String fullpath, String taskName, String message) {
        this.id = id;
        this.fullpath = fullpath;
        this.taskName = taskName;
        this.message = message;
    }
}
