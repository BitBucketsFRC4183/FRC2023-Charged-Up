package org.bitbuckets.lib.startup;

public interface ISetupDriver {

    int generateStartup(int processId, String name);
    void reportStartupProcessing(int id);
    void reportStartupError(int id, String error);
    void reportStartupInfo(int id, String info);
    void reportCompleted(int id);

    void generateStartupReport(); //Should return all the data

    class StartupReport {



    }

}
