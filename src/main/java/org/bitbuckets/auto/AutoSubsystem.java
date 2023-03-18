package org.bitbuckets.auto;

import com.pathplanner.lib.PathPlannerTrajectory;
import edu.wpi.first.wpilibj.DriverStation;
import org.bitbuckets.lib.core.HasLifecycle;
import org.bitbuckets.lib.core.HasLogLoop;
import org.bitbuckets.lib.core.HasLoop;
import org.bitbuckets.lib.log.IDebuggable;
import org.bitbuckets.lib.tune.IValueTuner;

import java.util.Optional;

public class AutoSubsystem implements HasLogLoop, HasLoop, HasLifecycle {

    final IValueTuner<AutoPath> pathToUse;
    final IAutoControl autoControl;
    final IDebuggable debug;

    public AutoSubsystem(IValueTuner<AutoPath> pathToUse, IAutoControl autoControl, IDebuggable debug) {
        this.pathToUse = pathToUse;
        this.autoControl = autoControl;
        this.debug = debug;
    }

    AutoPathInstance instance; //this is bad
    AutoFSM state = AutoFSM.DISABLED;

    public AutoFSM state() {
        return state;
    }

    public boolean hasChanged() {
        return hasChanged;
    }

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
        if (instance == null || state != AutoFSM.AUTO_RUN) {
            return Optional.empty();
        }

        return Optional.ofNullable(instance.sampleSpeeds());
    }


    int iteration = 0;

    boolean hasChanged = false;

    @Override
    public void loop() {


    }

    AutoPath toUseLogOnly = AutoPath.NONE;

    @Override
    public void logLoop() {
        debug.log("current-state", state);
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
        state = AutoFSM.TELEOP;
    }

    @Override
    public void autonomousPeriodic() {
        state = AutoFSM.AUTO_RUN;

        var opt = samplePathPlannerState();

        if (opt.isPresent()) {
            debug.log("path-pose", opt.get().poseMeters);
            debug.log("path-holonomic-rotation", opt.get().holonomicRotation.getDegrees());
        }


        hasChanged = false;
        switch (state) {
            case DISABLED:
                if (DriverStation.isTeleopEnabled() || DriverStation.isAutonomousEnabled()) {
                    hasChanged = true;
                    state = AutoFSM.INITIALIZATION;
                }
                break;
            case INITIALIZATION:
                if (DriverStation.isAutonomousEnabled()) {
                    autonomousInit();

                    state = AutoFSM.AUTO_RUN;
                    hasChanged = true;
                    break;
                }
                if (DriverStation.isTeleopEnabled()) {
                    state = AutoFSM.TELEOP;
                    hasChanged = true;
                    break;
                }
                break;
            case AUTO_RUN:

                if (DriverStation.isDisabled()) {
                    state = AutoFSM.DISABLED;
                    hasChanged = true;
                    break;
                }

                if (DriverStation.isTeleopEnabled()) {
                    state = AutoFSM.TELEOP;
                    hasChanged = true;
                    break;
                }
                if (instance.isDone()) {
                    instance.stop();
                    state = AutoFSM.AUTO_ENDED;
                    hasChanged = true;
                    break;
                }
                break;
            case AUTO_ENDED:
                if (DriverStation.isDisabled()) {
                    state = AutoFSM.DISABLED;
                    hasChanged = true;
                    break;
                }

                if (DriverStation.isTeleopEnabled()) {
                    state = AutoFSM.TELEOP;
                    hasChanged = true;
                    break;
                }
                break;
            case TELEOP:
                if (DriverStation.isDisabled()) {
                    state = AutoFSM.DISABLED;
                    hasChanged = true;
                    break;
                }
                //this can only happen in testing
                if (DriverStation.isAutonomousEnabled()) {
                    autonomousInit();

                    state = AutoFSM.AUTO_RUN;
                    hasChanged = true;
                    break;
                }
                break;
        }
    }
}
