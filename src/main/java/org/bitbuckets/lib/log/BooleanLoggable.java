package org.bitbuckets.lib.log;

import edu.wpi.first.networktables.GenericEntry;
import edu.wpi.first.util.datalog.BooleanLogEntry;

import java.util.concurrent.CompletableFuture;

public class BooleanLoggable extends AFutureLoggable<Boolean> {

    final BooleanLogEntry logEntry;

    public BooleanLoggable(CompletableFuture<GenericEntry> ftr, BooleanLogEntry logEntry) {
        super(ftr);
        this.logEntry = logEntry;
    }

    @Override
    void dealWith(Boolean data) {
        logEntry.append(data);
    }
}
