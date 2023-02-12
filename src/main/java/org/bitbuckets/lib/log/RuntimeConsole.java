package org.bitbuckets.lib.log;

import org.bitbuckets.lib.log.console.ConsoleRecord;
import org.bitbuckets.lib.log.console.IConsole;

import java.util.Queue;

public class RuntimeConsole implements IConsole {

    final int id;
    final Queue<ConsoleRecord> actions;

    public RuntimeConsole(int id, Queue<ConsoleRecord> actions) {
        this.id = id;
        this.actions = actions;
    }

    @Override
    public void sendInfo(String data) {
        //queue up a bunch of strings to be sent

        actions.add(new ConsoleRecord(id, ActionLevel.INFO, data, null));
    }

    @Override
    public void sendInfo(ActionLevel level, String data) {
        actions.add(new ConsoleRecord(id, level, data, null));
    }

    @Override
    public void sendError(Exception exception) {
        actions.add(new ConsoleRecord(id, ActionLevel.ERROR, null, exception));
    }
}
