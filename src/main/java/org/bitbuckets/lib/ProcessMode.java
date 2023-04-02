package org.bitbuckets.lib;

public enum ProcessMode {

    //more debug-sided


    DEBUG(1), //at this log level all logs will be reported to mattlib console
    COMPETITION(4) //at this log only errors and warnings will be logged


    ; //at this level loops will not run

    public final int level;

    ProcessMode(int level) {
        this.level = level;
    }
}
