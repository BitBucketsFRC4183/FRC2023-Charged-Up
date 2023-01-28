package org.bitbuckets.lib;

import org.bitbuckets.SimLevel;
import org.bitbuckets.lib.core.*;
import org.bitbuckets.lib.log.DataLogger;
import org.bitbuckets.lib.log.IDiffableData;
import org.bitbuckets.lib.startup.SetupDriver;
import org.bitbuckets.lib.tune.IValueTuner;

import java.util.function.Supplier;

//TODO documet
public class ProcessPath {

    final int currentId;

    final SetupDriver setupDriver;
    final IdentityDriver identityDriver;
    final ErrorDriver errorDriver;
    final LogDriver logDriver;
    final LoopDriver loopDriver;
    final TuneableDriver tuneableDriver;

    public ProcessPath(int currentId, SetupDriver setupDriver, IdentityDriver identityDriver, ErrorDriver errorDriver, LogDriver logDriver, LoopDriver loopDriver, TuneableDriver tuneableDriver) {
        this.currentId = currentId;
        this.setupDriver = setupDriver;
        this.identityDriver = identityDriver;
        this.errorDriver = errorDriver;
        this.logDriver = logDriver;
        this.loopDriver = loopDriver;
        this.tuneableDriver = tuneableDriver;
    }

    public SimLevel getSimLevel() {
        return SimLevel.SIM_CLASSES;
    }

    /**
     * You must call this if you are calling factories inside a factory
     *
     * @param name the name associated with this code module
     * @return a sub-tools build specifically for the child factory. You must pass it to the child factory.
     */

    public ProcessPath addChild(String name) {
        int childId = identityDriver.childProcess(currentId, name);

        return new ProcessPath(childId, setupDriver, identityDriver, errorDriver, logDriver, loopDriver, tuneableDriver);
    }


    /**
     * Register a loop with a default period of 100ms between runs
     * The normal loop runtime of the robot is 20ms, so if you need to update data every robot periodic,
     * use {@link #registerLoop(Runnable, int, String)}
     *
     * @param executable
     */
    public void registerLoop(Runnable executable, String friendlyName) {
        loopDriver.registerLoopPeriodic(currentId, executable, 100);
    }


    /**
     * Register a loop that will run all the time even when disabled.
     * If you're writing logic this is NOT what you want.
     * TODO document difference between this and sysstems
     *
     * @param executable
     * @param periodMs
     * @param friendlyName
     */
    public void registerLoop(Runnable executable, int periodMs, String friendlyName) {
        loopDriver.registerLoopPeriodic(currentId, executable, periodMs);
    }

    /**
     * Will only run in the sim but will run all the time
     *
     * @param runnable
     * @param friendlyname
     */
    public void registerSimLoop(Runnable runnable, String friendlyname) {
        loopDriver.registerLoopSimulation(currentId, runnable, 20);
    }


    public <A extends IDiffableData> DataLogger<A> generatePushDataLogger(Supplier<A> dataInitializer) {
        return new DataLogger<>(currentId, logDriver, dataInitializer.get());
    }


    public SetupProfiler generateSetupProfiler(String taskName) {
        int taskId = setupDriver.generateStartup(currentId, taskName);

        return new SetupProfiler(setupDriver, taskId);
    }


    /**
     * default value basically doesnt matter. Dont depend on it.
     * @param key
     * @param defaultData
     * @return
     * @param <T>
     */
    public <T> IValueTuner<T> generateValueTuner(String key, T defaultData) {
        return tuneableDriver.tuneable(currentId, key, defaultData);
    }





}
