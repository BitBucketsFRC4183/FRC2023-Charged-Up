package org.bitbuckets.lib.process;

public class ProcessQueueRecord {

    public final int processId;
    public final String word;
    public final Exception exception;

    public ProcessQueueRecord(int processId, String word, Exception exception) {
        this.processId = processId;
        this.word = word;
        this.exception = exception;
    }
}
