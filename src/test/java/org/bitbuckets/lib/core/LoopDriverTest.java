package org.bitbuckets.lib.core;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;

class LoopDriverTest {

    @Test
    void runAlways_shouldFailIfNotDelay() throws InterruptedException {
        LoopDriver loopDriver = new LoopDriver();
        AtomicBoolean run = new AtomicBoolean(false);
        loopDriver.registerLoopPeriodic(0, () -> {
            run.set(true);
        }, 100);

        Thread.sleep(90); //for some reason this only works at 90, if you make it 95 somehow it still passes.
        //could have to do with how long tests take to run.

        loopDriver.runPeriodic();
        Assertions.assertFalse(run.get());

    }

    @Test
    void runAlways_shouldRun() throws InterruptedException {
        LoopDriver loopDriver = new LoopDriver();
        AtomicBoolean run = new AtomicBoolean(false);
        loopDriver.registerLoopPeriodic(0, () -> {
            run.set(true);
        }, 100);

        Thread.sleep(101);

        loopDriver.runPeriodic();
        Assertions.assertTrue(run.get());

        run.set(false);
        Thread.sleep(101);

        loopDriver.runWhenSim();
        Assertions.assertFalse(run.get());

    }

    @Test
    void runWhenSim() throws InterruptedException {

        LoopDriver loopDriver = new LoopDriver();
        AtomicBoolean run = new AtomicBoolean(false);
        loopDriver.registerLoopSimulation(0, () -> {
            run.set(true);
        }, 100);

        Thread.sleep(101);

        loopDriver.runWhenSim();
        Assertions.assertTrue(run.get());

    }

}