package org.bitbuckets.lib.log;

import edu.wpi.first.networktables.GenericEntry;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class ScheduledLoggable<T> implements ILoggable<T> {

    final ScheduledFuture<GenericEntry> future;
    final Queue<T> queue = new ArrayDeque<>();

    public ScheduledLoggable(ScheduledFuture<GenericEntry> future) {
        this.future = future;
    }

    @Override
    public void log(T data) {
        if (!future.isDone()) {
            queue.add(data);
            return;
        }

        try {
            GenericEntry entry = future.get(1, TimeUnit.MILLISECONDS);

            entry.setValue(data);

            while (!queue.isEmpty()) {
                T in = queue.remove();

                entry.setValue(in);
            }
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new RuntimeException(e);
        }


    }
}
