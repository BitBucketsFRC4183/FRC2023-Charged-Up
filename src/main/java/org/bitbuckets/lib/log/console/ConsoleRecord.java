package org.bitbuckets.lib.log.console;

import org.bitbuckets.lib.log.ActionLevel;

public class ConsoleRecord {

    public final int processId;
    public final ActionLevel actionLevel;
    public final String data_Nullable; //nullable
    public final Exception exception_Nullable;

    public ConsoleRecord(int processId, ActionLevel actionLevel, String data_Nullable, Exception exception_Nullable) {
        this.processId = processId;
        this.actionLevel = actionLevel;
        this.data_Nullable = data_Nullable;
        this.exception_Nullable = exception_Nullable;
    }
}
