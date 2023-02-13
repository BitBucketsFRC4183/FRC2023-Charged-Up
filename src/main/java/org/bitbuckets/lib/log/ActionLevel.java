package org.bitbuckets.lib.log;

public enum ActionLevel {

    ERROR(0),
    INFO(1);

    public final int level;

    ActionLevel(int level) {
        this.level = level;
    }
}
