package org.bitbuckets.auto;

import com.pathplanner.lib.PathPlannerTrajectory;
import edu.wpi.first.wpilibj.Timer;

import java.util.List;
import java.util.Map;

public class AutoPathInstance {

    final List<PathPlannerTrajectory> segments;
    final Map<String, Double> eventToTimeMap;
    final List<AutoPathInstance.SegmentTime> segmentTimes; //needs to be inserted highest back
    final AutoPath type;
    final double totalTime;


    public void start() {
        pathTimer.start();
    }

    public void stop() {
        pathTimer.stop();
    }


    record SegmentTime(int index, double startTime, boolean stopped) {
    }

    public AutoPathInstance(List<PathPlannerTrajectory> segments, Map<String, Double> eventToTimeMap, List<SegmentTime> segmentTimes, AutoPath type, double totalTime) {
        this.segments = segments;
        this.eventToTimeMap = eventToTimeMap;
        this.segmentTimes = segmentTimes;
        this.type = type;
        this.totalTime = totalTime;


    }

    //this is dumb

    Timer pathTimer = new Timer();

    public SegmentTime getSegmentTime(double currentTime) {
        for (int i = segmentTimes.size() - 1; i >= 0; i--) { //latest startTime must come first
            if (currentTime >= segmentTimes.get(i).startTime) {
                return segmentTimes.get(i);
            }
        }

        return new SegmentTime(0, 0, true);
    }


    /**
     * @param eventName the event of the path
     * @return whether it has happened yet
     */
    public boolean sampleHasEventStarted(String eventName) {
        if (type == AutoPath.NONE) {
            return false;
        }

        double secondsNow = pathTimer.get();
        if (eventToTimeMap.get(eventName) == null) return false;

        return secondsNow >= eventToTimeMap.get(eventName);
    }


    public PathPlannerTrajectory.PathPlannerState sampleSpeeds() {
        if (type == AutoPath.NONE) {
            return null;
        }

        double secondsNow = pathTimer.get();
        SegmentTime lastTimestamp = getSegmentTime(secondsNow);
        double secondsInSegment = secondsNow - lastTimestamp.startTime;

        if (lastTimestamp.stopped()) {
            return null;
        }

        return (PathPlannerTrajectory.PathPlannerState) segments.get(lastTimestamp.index).sample(secondsInSegment);
    }

    public boolean isDone() {
        return pathTimer.hasElapsed(totalTime);
    }


}
