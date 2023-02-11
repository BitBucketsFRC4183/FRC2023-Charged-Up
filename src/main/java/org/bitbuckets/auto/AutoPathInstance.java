package org.bitbuckets.auto;

import com.pathplanner.lib.PathPlannerTrajectory;
import edu.wpi.first.wpilibj.Timer;
import org.bitbuckets.lib.util.HasLifecycle;

import java.util.Map;

public class AutoPathInstance implements HasLifecycle {

    final PathPlannerTrajectory trajectory;
    final Map<String, Double> eventToTimeMap;
    final double pathTime_seconds;
    final AutoPath type;

    AutoPathInstance(PathPlannerTrajectory trajectory, Map<String, Double> eventToTimeMap, double pathTime_seconds, AutoPath type) {
        this.trajectory = trajectory;
        this.eventToTimeMap = eventToTimeMap;
        this.pathTime_seconds = pathTime_seconds;
        this.type = type;
    }

    final Timer timer = new Timer();


    /**
     * @param eventName the event of the path
     * @return whether it has happened yet
     */
    public boolean sampleHasEventStarted(String eventName) {
        double secondsNow = timer.get();

        if (eventToTimeMap.get(eventName) == null) return false;

        return secondsNow > eventToTimeMap.get(eventName);
    }

    public PathPlannerTrajectory.PathPlannerState sampleSpeeds() {
        double secondsNow = timer.get();

        return (PathPlannerTrajectory.PathPlannerState) trajectory.sample(secondsNow);
    }

    public boolean isDone() {
        return timer.hasElapsed(pathTime_seconds);
    }


    @Override
    public void start() {
        timer.start();
    }

    @Override
    public void end() {
        timer.stop();
    }
}
