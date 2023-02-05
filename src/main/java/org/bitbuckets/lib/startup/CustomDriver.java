package org.bitbuckets.lib.startup;

import org.bitbuckets.lib.core.IdentityDriver;
import org.littletonrobotics.junction.Logger;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

public class CustomDriver implements ISetupDriver, Runnable {

    final Queue<SetupRecord> consoleReports = new ArrayDeque<>();
    final Map<Integer, DataRecord> map = new HashMap<>();
    int current = 0;

    final IdentityDriver driver;
    final Logger logger;

    public CustomDriver(IdentityDriver driver, Logger logger) {
        this.driver = driver;
        this.logger = logger;
    }

    int nextId() {
        return ++current;
    }

    class DataRecord {
        final int ownerId;

        public DataRecord(int ownerId) {
            this.ownerId = ownerId;
        }

        long report = 0;
    }

    @Override
    public int generateStartup(int processId, String name) {
        int alloc = nextId();
        map.put(alloc, new DataRecord(processId));

        return 0;
    }

    @Override
    public void reportStartupProcessing(int id) {
        DataRecord dt = map.get(id);//TODO

        driver.fullPath(dt.ownerId);
    }

    @Override
    public void reportStartupError(int id, String error) {

    }

    @Override
    public void reportStartupInfo(int id, String info) {

    }

    @Override
    public void reportCompleted(int id) {

    }

    @Override
    public void generateStartupReport() {

    }

    @Override
    public void run() {
        //for every record in the list
        //append to the console and
        //push to the network tables

        StringBuilder report = new StringBuilder();
        Map<Integer, StringBuilder> thisIsBad = new HashMap<>();

        while (!consoleReports.isEmpty()) {
            //add to console report

            SetupRecord record = consoleReports.poll();

            String consolePost = String.format(
                    "[%s] (Task %s): %s",
                    record.fullpath,
                    record.taskName,
                    record.message
            );


            report.append(consolePost).append("\n");
            //send to self report
        }
    }
}
