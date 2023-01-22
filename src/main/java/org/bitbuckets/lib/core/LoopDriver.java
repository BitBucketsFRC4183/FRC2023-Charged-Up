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

    final List<Runnable> alwaysRun = new ArrayList<>();
    final List<Runnable> simulationRun = new ArrayList<>();

    public int registerRunnable(int parentId, Runnable exe, int delay_ms) {
        alwaysRun.add(exe);
        return 0;
    }

    public int registerLoopSimulation(int parentId, Runnable loop, int period_ms) {
        simulationRun.add(loop);
        return 0;

    }

    public int registerLoopTeleop(int parentId, Runnable loop, int period_ms) {
        alwaysRun.add(loop);
        return 0;

    }

    public int registerLoopPeriodic(int parentId, Runnable loop, int period_ms) {
        alwaysRun.add(loop);
        return 0;

    }

}
