package org.bitbuckets.lib.log;

public interface IConsole {

    void sendInfo(String data); //will just log to the data with level INFO

    void sendError(Exception exception); // will swallow and then raise a problem
    void sendWarning(String data);

}
