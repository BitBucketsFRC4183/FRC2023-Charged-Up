package org.bitbuckets.lib.log;

import edu.wpi.first.networktables.GenericEntry;

import java.util.concurrent.CompletableFuture;

public abstract class AFutureLoggable<T> implements ILoggable<T> {

    final CompletableFuture<GenericEntry> ftr;

    public AFutureLoggable(CompletableFuture<GenericEntry> ftr) {
        this.ftr = ftr;
    }

    abstract void dealWith(T data);

    @Override
    public void log(T data) {
        dealWith(data);
        if (ftr.isDone()) {
            ftr.join().setValue(data);
        } else {
            ftr.thenAccept(e->e.setValue(data));
        }
    }
}
