package org.bitbuckets.lib.log;

import org.bitbuckets.lib.core.Path;

public class LogRecord {

    public final Path key;
    public final String info;
    public final Exception exception;

    public LogRecord(Path key, String info, Exception exception) {
        this.key = key;
        this.info = info;
        this.exception = exception;
    }
}
