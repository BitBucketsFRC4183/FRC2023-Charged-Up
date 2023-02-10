package org.bitbuckets.lib.startup;

import org.bitbuckets.lib.core.IdentityDriver;
import org.littletonrobotics.junction.Logger;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

import static java.lang.String.format;

public class StartupDriver implements IStartupDriver, Runnable {

    final Queue<SetupRecord> consoleReports = new ArrayDeque<>();
    final Queue<ErrorRecord> errorReports = new ArrayDeque<>();

    final Map<Integer, DataRecord> map = new HashMap<>();
    int current = 0;

    final IdentityDriver driver;
    final Logger logger;

    boolean exceptions = false;

    public StartupDriver(IdentityDriver driver, Logger logger) {
        this.driver = driver;
        this.logger = logger;
    }

    int nextId() {
        current++;

        return current;
    }

    static class DataRecord {
        final String fullpath;
        final String taskName;
        final int ownerId;

        public DataRecord(String fullpath, String taskName, int ownerId) {
            this.fullpath = fullpath;
            this.taskName = taskName;
            this.ownerId = ownerId;
        }

        long report = 0;
    }

    int called = 0;

    @Override
    public int generateStartup(int processId, String name) {
        int alloc = nextId();
        String fullPath = driver.fullPath(processId);

        map.put(
                alloc,
                new DataRecord(
                        fullPath, //don't try to get the full path of alloc
                        name,
                        processId
                )
        );

        consoleReports.add(
                new SetupRecord(alloc, fullPath, name, "MODE Uninitialized")
        );

        return alloc;
    }

    @Override
    public void reportStartupProcessing(int id) {
        DataRecord dt = map.get(id);//TODO

        dt.report = System.currentTimeMillis();

        consoleReports.add(
                new SetupRecord(
                        id,
                        dt.fullpath,
                        dt.taskName,
                        "MODE Processing")
        );
    }

    @Override
    public void reportStartupError(int id, Throwable error) {
        DataRecord record = map.get(id);

        long delta = System.currentTimeMillis() - record.report;

        consoleReports.add(
                new SetupRecord(
                        id,
                        record.fullpath,
                        record.taskName,
                        "MODE Failed")
        );

        consoleReports.add(
                new SetupRecord(
                        id,
                        record.fullpath,
                        record.taskName,
                        format("Failed in (%s ms)", delta))
        );

        //TODO update this
        errorReports.add(new ErrorRecord(record.fullpath, record.taskName, error));
    }

    @Override
    public void reportStartupInfo(int id, String info) {
        DataRecord record = map.get(id);

        consoleReports.add(
                new SetupRecord(
                        id,
                        record.fullpath,
                        record.taskName,
                        info)
        );
    }

    @Override
    public void reportCompleted(int id) {
        DataRecord dt = map.get(id);

        long delta = System.currentTimeMillis() - dt.report;

        consoleReports.add(
                new SetupRecord(
                        id,
                        dt.fullpath,
                        dt.taskName,
                        "MODE Completed")
        );

        consoleReports.add(
                new SetupRecord(
                        id,
                        dt.fullpath,
                        dt.taskName,
                        format("Booted in (%s ms)", delta))
        );
    }

    @Override
    public void generateStartupReport() {
        StringBuilder errorReport = new StringBuilder();

        while (!errorReports.isEmpty()) {
            exceptions = true;
            //add to console report

            ErrorRecord record = errorReports.poll();
            StackTraceElement element = record.exception.getStackTrace()[0];
            String message = record.exception.getMessage();
            int lineNumber = element.getLineNumber();
            String methodName = element.getMethodName();

            logger.recordOutput("anal", record.processName + record.taskName + methodName + lineNumber + message);

            String consolePost = format(
                    "[%s] (Task %s): Threw exception at ('%s' line %s), report: %s",
                    record.processName,
                    record.taskName,
                    methodName,
                    lineNumber,
                    message
            );

            System.out.println(consolePost);

            errorReport.append(consolePost).append("\n");
        }

        if (exceptions) {
            exceptions = false;
            logger.recordOutput("MattConsole/Errors", errorReport.toString());
        }


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

            String consolePost = format(
                    "[%s] (Task %s): %s",
                    record.processPath,
                    record.taskName,
                    record.message
            );

            report.append(consolePost).append("\n");
            thisIsBad.computeIfAbsent(record.taskId, id -> new StringBuilder()).append(consolePost).append("\n");
        }


        if (report.length() > 0) {
            logger.recordOutput("MattConsole/General", report.toString());
        }


        for (Map.Entry<Integer, StringBuilder> e : thisIsBad.entrySet()) {
            int id = e.getKey();
            StringBuilder builder = e.getValue();

            if (builder.length() > 0) {

                String fullExtension = map.get(id).fullpath + "/" + map.get(id).taskName;

                logger.recordOutput("MattConsole/" + fullExtension, builder.toString());
            }

        }

    }
}
