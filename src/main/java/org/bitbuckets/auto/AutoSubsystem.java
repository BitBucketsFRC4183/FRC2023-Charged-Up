package org.bitbuckets.auto;

import com.pathplanner.lib.PathPlannerTrajectory;
import org.bitbuckets.lib.core.HasLifecycle;
import org.bitbuckets.lib.core.HasLogLoop;
import org.bitbuckets.lib.log.IDebuggable;
import org.bitbuckets.lib.tune.IValueTuner;

import java.util.Optional;

public class AutoSubsystem implements HasLogLoop, HasLifecycle {

    final IValueTuner<AutoPath> pathToUse;
    final IAutoControl autoControl;
    final IDebuggable debug;

    public AutoSubsystem(IValueTuner<AutoPath> pathToUse, IAutoControl autoControl, IDebuggable debug) {
        this.pathToUse = pathToUse;
        this.autoControl = autoControl;
        this.debug = debug;
    }

    AutoPathInstance instance; //this is bad


    public boolean sampleHasEventStarted(String event) {
        if (instance == null) {


            return false; //Doesn't exist yet. Should log this.
        }

        return instance.sampleHasEventStarted(event);
    }

    /**
     * @return state if in auto, otherwise an empty optional
     */
    public Optional<PathPlannerTrajectory.PathPlannerState> samplePathPlannerState() {
        if (instance == null) {
            return Optional.empty();
        }

        return Optional.ofNullable(instance.sampleSpeeds());
    }


    int iteration = 0;

    AutoPath toUseLogOnly = AutoPath.NONE;

    @Override
    public void logLoop() {
        debug.log("actual-path", toUseLogOnly);
        debug.log("dashboard-path", pathToUse.readValue());

    }

    @Override
    public void autonomousInit() {
        AutoPath toUse = pathToUse.readValue();
        toUseLogOnly = toUse;
        instance = autoControl.generateAndStartPath(toUse);
        iteration++;
    }


    @Override
    public void teleopInit() {
        if (instance != null) {
            instance.stop();
        }
    }

    @Override
    public void disabledInit() {
        if (instance != null) {
            instance.stop();
        }
    }

    @Override
    public void autonomousPeriodic() {

        var opt = samplePathPlannerState();

        if (opt.isPresent()) {
            debug.log("path-pose", opt.get().poseMeters);
            debug.log("path-holonomic-rotation", opt.get().holonomicRotation.getDegrees());
        }


        if (instance.isDone()) {
            instance.stop();
        }
    }
}
