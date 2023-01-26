package org.bitbuckets.lib.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * i will remove it.
 * <p>
 * TODO make this class follow it's contracts
 */
public class LoopDriver {

    final Map<Integer, Integer> mapLoopIdToParentId = new HashMap<>(); //TODO

    static class Record {
        final Runnable runnable;
        final long delayMs;

        long lastDelayMs = System.currentTimeMillis();

        Record(Runnable runnable, long delayMs) {
            this.runnable = runnable;
            this.delayMs = delayMs;
        }
    }

    final List<Record> loopWhenSim = new ArrayList<>();
    final List<Record> loopWhenOn = new ArrayList<>();


    public void registerLoopSimulation(int parentId, Runnable loop, int period_ms) {
        loopWhenSim.add(new Record(loop, period_ms));

    }

    public void registerLoopPeriodic(int parentId, Runnable loop, int period_ms) {
        loopWhenOn.add(new Record(loop,period_ms));

    }

    public void runAlways() {
        long now = System.currentTimeMillis();

        for (Record record : loopWhenOn) {
            if (now - record.lastDelayMs >= record.delayMs) {
                record.lastDelayMs = now;

                record.runnable.run();
            }
        }
    }

    public void runWhenSim() {
        long now = System.currentTimeMillis();

        for (Record record : loopWhenSim) {
            if (now - record.lastDelayMs >= record.delayMs) {
                record.lastDelayMs = now;

                record.runnable.run();
            }
        }
    }

}
