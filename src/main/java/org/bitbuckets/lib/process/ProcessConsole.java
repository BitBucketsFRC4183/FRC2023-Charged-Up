package org.bitbuckets.lib.process;

import org.bitbuckets.lib.log.ActionLevel;
import org.bitbuckets.lib.log.console.IConsole;
import org.bitbuckets.lib.tune.IValueTuner;

import java.util.Queue;

public class ProcessConsole implements IConsole {

    final IValueTuner<ActionLevel> levelTuner;
    final Queue<ProcessQueueRecord> queue;
    final int id;


    public ProcessConsole(IValueTuner<ActionLevel> levelTuner, Queue<ProcessQueueRecord> queue, int id) {
        this.levelTuner = levelTuner;
        this.queue = queue;
        this.id = id;
    }

    @Override
    public void sendInfo(String data) {
        if (levelTuner.readValue().level >= ActionLevel.INFO.level) {
            queue.add(new ProcessQueueRecord(id, data, null));
        }
    }

    @Override
    public void sendInfo(ActionLevel level, String data) {
        if (levelTuner.readValue().level >= level.level) {
            queue.add(new ProcessQueueRecord(id, data, null));
        }
    }

    @Override
    public void sendError(Exception exception) {
        queue.add(new ProcessQueueRecord(id, null, exception));
    }
}
