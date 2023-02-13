package org.bitbuckets.auto;

import com.pathplanner.lib.PathPlannerTrajectory;
import edu.wpi.first.wpilibj.DriverStation;
import org.bitbuckets.drive.IDriveControl;
import org.bitbuckets.lib.log.Debuggable;
import org.bitbuckets.lib.tune.IValueTuner;
import org.bitbuckets.odometry.IOdometryControl;

import java.util.Optional;

public class AutoSubsystem {

    final IValueTuner<AutoPath> pathToUse;
    final IAutoControl autoControl;
    final Debuggable debug;

    final AutoInput autoInput;

    IDriveControl driveControl;

    IOdometryControl odometryControl;

    public AutoSubsystem(IValueTuner<AutoPath> pathToUse, IAutoControl autoControl, AutoInput autoInput, Debuggable debug) {
        this.pathToUse = pathToUse;
        this.autoControl = autoControl;
        this.autoInput = autoInput;
        this.debug = debug;
    }

    AutoPathInstance instance;

    AutoFSM state = AutoFSM.DISABLED;

    public AutoFSM state() {
        return state;
    }

    public boolean sampleHasEventStarted(String event) {
        if (instance == null) {
            debug.out("Waiting...");
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

        return Optional.of(instance.sampleSpeeds());
    }


    int iteration = 0;

    public void runLoop() {
        switch (state) {
            case DISABLED:
                if (DriverStation.isAutonomousEnabled()) {
                    transitionToAutoRun();

                    state = AutoFSM.AUTO_RUN;
                    break;
                }
                if (DriverStation.isTeleopEnabled()) {
                    state = AutoFSM.TELEOP;
                    break;
                }
                break;
            case AUTO_RUN:

                if (DriverStation.isTeleopEnabled()) {

                    state = AutoFSM.TELEOP;
                    break;
                }
                if (instance.isDone()) {
                    instance.end();
                    state = AutoFSM.AUTO_ENDED;
                    break;
                }
                break;
            case AUTO_ENDED:
                if (DriverStation.isTeleopEnabled()) {
                    state = AutoFSM.TELEOP;
                    break;
                }
                break;
            case TELEOP:
                if (DriverStation.isDisabled()) {
                    state = AutoFSM.DISABLED;
                    break;
                }
                //this can only happen in testing
                if (DriverStation.isAutonomousEnabled()) {
                    transitionToAutoRun();

                    state = AutoFSM.AUTO_RUN;
                    break;
                }
                break;
        }
    }

    AutoPath toUseLogOnly = AutoPath.NONE;

    void transitionToAutoRun() {


        AutoPath toUse = pathToUse.readValue();
        toUseLogOnly = toUse;
        instance = autoControl.generateAndStartPath(toUse, driveControl.currentPositions(), odometryControl);
        iteration++;

    }

    void logLoop() {
        debug.log("current-state", state);
        debug.log("actual-path", toUseLogOnly);
        debug.log("dashboard-path", pathToUse.readValue());
    }

    public void setDriveControl(IDriveControl driveControl) {
        this.driveControl = driveControl;
    }

    public void setOdometryControl(IOdometryControl odometryControl) {
        this.odometryControl = odometryControl;
    }
}
