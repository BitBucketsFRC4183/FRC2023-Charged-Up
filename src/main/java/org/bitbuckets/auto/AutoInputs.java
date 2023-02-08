package org.bitbuckets.auto;

import com.pathplanner.lib.PathPlannerTrajectory;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj.Timer;
import org.bitbuckets.lib.tune.IValueTuner;

/**
 * Mock inputs class that lets us know if events have passed yet
 */
public class AutoInputs {

    final PathPlannerTrajectory[] trajectories;
    final IValueTuner<AutoPath> pathToUse;

    final Timer pooledTimer = new Timer();

    public AutoInputs(PathPlannerTrajectory[] trajectories, IValueTuner<AutoPath> pathToUse) {
        this.trajectories = trajectories;
        this.pathToUse = pathToUse;
    }

    /**
     *
     * @param eventName the event of the path
     * @return whether it has happened yet
     */
    public boolean sampleHasEventStarted(String eventName) {


        return false;
    }

    public ChassisSpeeds sampleChassisSpeeds() {
        return null;
    }



    public void markStarted() {
        pooledTimer.start();
    }

    public void markStopped() {
        pooledTimer.stop();
        pooledTimer.reset();
    }

}
