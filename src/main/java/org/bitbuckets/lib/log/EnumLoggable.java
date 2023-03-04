package org.bitbuckets.lib.log;

import edu.wpi.first.networktables.GenericEntry;

import java.util.concurrent.CompletableFuture;

public class EnumLoggable<T extends Enum<T>> implements ILoggable<T> {

    final CompletableFuture<GenericEntry> ftr;

    public EnumLoggable(CompletableFuture<GenericEntry> ftr) {
        this.ftr = ftr;
    }

    @Override
    public void log(T data) {
        if (ftr.isDone()) {
            ftr.join().setValue(data.name());
        } else {
            ftr.thenAccept(e->e.setValue(data.name()));
        }
    }

}
