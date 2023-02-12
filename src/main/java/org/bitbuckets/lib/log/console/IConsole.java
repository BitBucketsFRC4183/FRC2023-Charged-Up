package org.bitbuckets.lib.log.console;

import org.bitbuckets.lib.log.ActionLevel;

public interface IConsole {

    void sendInfo(String data); //will just log to the data with level INFO
    void sendInfo(ActionLevel level, String data);

    void sendError(Exception exception); // will swallow and then raise a problem

}
