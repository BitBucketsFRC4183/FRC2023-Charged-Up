package org.bitbuckets.lib.startup;

import org.bitbuckets.lib.core.IdentityDriver;
import org.bitbuckets.lib.core.LogDriver;

import java.util.HashMap;
import java.util.Map;

public class SetupDriver implements ISetupDriver {

    final IdentityDriver identityDriver;
    final LogDriver logDriver;
    final int startupConsoleId;

    final Map<Integer, StartupRecord> telemetry = new HashMap<>();

    int counter = 0;
    int nextId() {
        counter++;
        return counter;
    }

    public SetupDriver(IdentityDriver identityDriver, LogDriver logDriver, int startupConsoleId) {
        this.identityDriver = identityDriver;
        this.logDriver = logDriver;
        this.startupConsoleId = startupConsoleId;
    }


    static class StartupRecord {
        final int processId;
        final String processName;
        final String taskName;

        public StartupRecord(int processId, String processName, String taskName) {
            this.processId = processId;
            this.processName = processName;
            this.taskName = taskName;
        }

        StartupState state = StartupState.UNINITIALIZED;
        long initTime;
    }



    @Override
    public int generateStartup(int processId, String taskName) {
        int alloc = nextId();
        telemetry.put(alloc, new StartupRecord(processId, identityDriver.fullPath(processId), taskName));

        consoleReport(alloc, "Uninitialized");
        processReport(alloc, StartupState.UNINITIALIZED);

        return alloc;
    }

    @Override
    public void reportStartupProcessing(int id) {
        processReport(id, StartupState.PROCESSING);
        consoleReport(id, "Processing");

        StartupRecord rec = telemetry.get(id);

        rec.state = StartupState.PROCESSING;
        rec.initTime = System.currentTimeMillis();
    }

    @Override
    public void reportStartupError(int id, String error) {
        processReport(id, StartupState.FAILED);
        StartupRecord rec = telemetry.get(id);
        rec.state = StartupState.FAILED;
        long time = System.currentTimeMillis() - rec.initTime;

        consoleReport(id, String.format("Failed in (%s): %s", time, error));
    }

    @Override
    public void reportStartupInfo(int id, String info) {
        consoleReport(id, info);
    }

    @Override
    public void reportCompleted(int id) {
        if (telemetry.get(id).state == StartupState.FAILED) return;

        processReport(id, StartupState.COMPLETED);

        StartupRecord rec = telemetry.get(id);
        rec.state = StartupState.COMPLETED;
        long time = System.currentTimeMillis() - rec.initTime;

        consoleReport(id, String.format("Completed in (%s)", time));
    }

    @Override
    public void generateStartupReport() {
        consoleDump();
    }

    void processReport(int taskId, StartupState blah) {
        StartupRecord record = telemetry.get(taskId);

        logDriver.report(record.processId, StartupConstants.SETUP_PREFIX + record.taskName + StartupConstants.STATUS_SUFFIX, blah.toString());
    }

    void consoleReport(int taskId, String str) {
        StartupRecord record = telemetry.get(taskId);
        if (record == null) throw new IllegalStateException("somehow, startup record is null");

        String consolePost = String.format(
                "[%s] (Task %s): %s",
                identityDriver.fullPath(record.processId),
                record.taskName,
                str
        );


        logDriver.report(startupConsoleId, "INFO", consolePost);
    }

    public void consoleDump() {
        //dump every still processing action into the console

        StringBuilder report = new StringBuilder();

        report.append("The following tasks failed to start: \n\n");

        boolean failed = false;

        for (Map.Entry<Integer, StartupRecord> entry : telemetry.entrySet()) {
            StartupRecord record = entry.getValue();

            if (record.state == StartupState.PROCESSING) {
                failed = true;

                report.append(String.format("[%s] (Task %s)\n", record.processName, record.taskName));
            }

        }

        if (!failed) {
            report.append("All tasks started successfully!");
        }

        logDriver.report(startupConsoleId, "INCOMPLETE", report.toString());
    }
}
