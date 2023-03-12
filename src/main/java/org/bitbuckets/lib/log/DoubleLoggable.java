package org.bitbuckets.lib.log;

import edu.wpi.first.networktables.GenericEntry;
import edu.wpi.first.util.datalog.DoubleLogEntry;

import java.util.concurrent.CompletableFuture;

public class DoubleLoggable extends AFutureLoggable<Double> {

    final DoubleLogEntry doubleLogEntry;

    public DoubleLoggable(CompletableFuture<GenericEntry> ftr, DoubleLogEntry doubleLogEntry) {
        super(ftr);
        this.doubleLogEntry = doubleLogEntry;
    }

    @Override
    void dealWith(Double data) {
        doubleLogEntry.append(data);
    }


}
