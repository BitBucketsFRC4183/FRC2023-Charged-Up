package org.bitbuckets.lib;

public enum ProcessMode {

    //more debug-sided


    TUNE(0), //enables all debug logging AND tuning

    LOG_DEBUG(1), //at this log level all logs will be reported to mattlib console
    LOG_PROFILING(2), //at this log level log loops will run
    LOG_INFO(3),
    LOG_COMPETITION(4), //at this log only errors and warnings will be logged
    LOG_ERROR(5), //at this log level only errors will be reported to mattlib console


    //below here running does not happen
    DISABLED(999); //at this level loops will not run

    public final int level;

    ProcessMode(int level) {
        this.level = level;
    }
}
