package org.bitbuckets.lib.log;

import org.bitbuckets.lib.core.Path;
import org.bitbuckets.lib.ProcessMode;
import org.bitbuckets.lib.tune.IValueTuner;

import java.util.ArrayDeque;
import java.util.Queue;

public class ProcessConsole implements IConsole {

    public static final Queue<LogRecord> QUEUE = new ArrayDeque<>();

    final IValueTuner<ProcessMode> shouldIBother;
    final Path path;

    //TODO rate limiting


    public ProcessConsole(IValueTuner<ProcessMode> shouldIBother, Path path) {
        this.shouldIBother = shouldIBother;
        this.path = path;
    }

    @Override
    public void sendInfo(String data) {
        if (shouldIBother.readValue().level >= ProcessMode.LOG_INFO.level) {
            QUEUE.add(new LogRecord(path, data, null));
        }
    }

    @Override
    public void sendError(Exception exception) {
        QUEUE.add(new LogRecord(path, null, exception));
    }

    @Override
    public void sendWarning(String data) {
        QUEUE.add(new LogRecord(path, data, null));
    }
}
