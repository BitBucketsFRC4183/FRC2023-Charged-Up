package org.bitbuckets.lib.log;

import edu.wpi.first.networktables.GenericEntry;
import edu.wpi.first.util.datalog.StringLogEntry;

import java.util.concurrent.CompletableFuture;

public class EnumLoggable<T extends Enum<T>> implements ILoggable<T>{

    final CompletableFuture<GenericEntry>ftr;
    final StringLogEntry entry;

    public EnumLoggable(CompletableFuture<GenericEntry> ftr, StringLogEntry entry) {
        this.ftr = ftr;
        this.entry = entry;
    }

    @Override
    public void log(T data) {
        if (ftr.isDone()) {
            ftr.join().setValue(data.toString());
        } else {
            ftr.thenAccept(e->e.setValue(data.toString()));
        }
    }
}
