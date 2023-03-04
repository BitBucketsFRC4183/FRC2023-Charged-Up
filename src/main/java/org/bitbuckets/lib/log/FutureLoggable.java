package org.bitbuckets.lib.log;

import edu.wpi.first.networktables.GenericEntry;

import java.util.concurrent.CompletableFuture;

public class FutureLoggable<T> implements ILoggable<T> {

    final CompletableFuture<GenericEntry> ftr;

    public FutureLoggable(CompletableFuture<GenericEntry> ftr) {
        this.ftr = ftr;
    }

    @Override
    public void log(T data) {
        if (ftr.isDone()) {
            ftr.join().setValue(data);
        } else {
            ftr.thenAccept(e->e.setValue(data));
        }
    }
}
