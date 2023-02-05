package org.bitbuckets.lib.startup;

import org.bitbuckets.lib.DontUseIncubating;

public interface IStartupDriver {

    int generateStartup(int processId, String name);
    void reportStartupProcessing(int id);
    void reportStartupError(int id, Throwable error);
    void reportStartupInfo(int id, String info);
    void reportCompleted(int id);

    void generateStartupReport(); //Should return all the data

}
