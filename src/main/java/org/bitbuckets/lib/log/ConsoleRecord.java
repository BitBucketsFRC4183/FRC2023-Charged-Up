package org.bitbuckets.lib.log;

import org.bitbuckets.lib.ProcessMode;

public class ConsoleRecord {

    public final int processId;
    public final ProcessMode processMode;
    public final String data_Nullable; //nullable
    public final Exception exception_Nullable;

    public ConsoleRecord(int processId, ProcessMode processMode, String data_Nullable, Exception exception_Nullable) {
        this.processId = processId;
        this.processMode = processMode;
        this.data_Nullable = data_Nullable;
        this.exception_Nullable = exception_Nullable;
    }
}
