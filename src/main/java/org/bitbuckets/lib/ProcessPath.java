package org.bitbuckets.lib;

import edu.wpi.first.math.kinematics.SwerveModuleState;
import org.bitbuckets.SimLevel;
import org.bitbuckets.lib.core.IdentityDriver;
import org.bitbuckets.lib.core.LogDriver;
import org.bitbuckets.lib.core.LoopDriver;
import org.bitbuckets.lib.log.ILoggable;
import org.bitbuckets.lib.log.type.DataLoggable;
import org.bitbuckets.lib.log.type.DoubleLoggable;
import org.bitbuckets.lib.log.type.StateLoggable;
import org.bitbuckets.lib.log.type.StringLoggable;
import org.bitbuckets.lib.startup.SetupDriver;
import org.bitbuckets.lib.tune.IValueTuner;
import org.bitbuckets.lib.tune.TuneableDriver;

//TODO documet
public class ProcessPath {

    final int currentId;

    final SetupDriver setupDriver;
    final IdentityDriver identityDriver;
    final LogDriver logDriver;
    final LoopDriver loopDriver;
    final TuneableDriver tuneableDriver;

    public ProcessPath(int currentId, SetupDriver setupDriver, IdentityDriver identityDriver, LogDriver logDriver, LoopDriver loopDriver, TuneableDriver tuneableDriver) {
        this.currentId = currentId;
        this.setupDriver = setupDriver;
        this.identityDriver = identityDriver;
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

        return new ProcessPath(childId, setupDriver, identityDriver, logDriver, loopDriver, tuneableDriver);
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




    @Deprecated @DontUseIncubating
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

    /**
     * Generates a tuneable that represents multiplie pieces of data that can be independently tuned,
     * same semantics as generating a bunch of double tuneables and checking them individually
     * @param keys list of the dashboard names of your data in order
     * @param defaultDatas the default data in the same order as the keys
     * @return a value tuner returning a double with same order as the keys
     */
    public IValueTuner<double[]> generateMultiTuner(String[] keys, double[] defaultDatas) {
        return tuneableDriver.multiTuneable(currentId, keys, defaultDatas);
    }


    /**
     * Generates a loggable that logs doubles. You will have to call log on it to send data
     * @param name the name it should show up as in logs
     * @return loggable
     */
    public ILoggable<Double> generateDoubleLogger(String name) {
        return new DoubleLoggable(logDriver, currentId, name);
    }

    /**
     * Generates a loggable that logs bools. You will have to call log on it to send data
     * @param name the name it should show up as in logs
     * @return loggable
     */
    public ILoggable<Boolean> generateBooleanLogger(String name) {
        return data -> logDriver.report(currentId, name, data);
    }

    /**
     * Generates a loggable that logs double arrays. You will have to call log on it to send data
     * @param namesInOrder the names of the data in order, your .log method should add data in the same order as this array
     * @return loggable
     */
    public ILoggable<double[]> generateDoubleLoggers(String... namesInOrder) {
        return new DataLoggable(namesInOrder, logDriver, currentId);
    }

    /**
     * swerving on mattlib
     * @param name
     * @return
     */
    public ILoggable<SwerveModuleState[]> generateStateLogger(String name) {

        return new StateLoggable(logDriver, currentId, name);

    }


    /**
     * read the other ones please
     * @param name
     * @return
     */
    public ILoggable<String> generateStringLogger(String name) {
        return new StringLoggable(logDriver, currentId, name);

    }
}
