package org.bitbuckets.auto;

import com.pathplanner.lib.PathPlannerTrajectory;
import edu.wpi.first.wpilibj.Timer;
import org.bitbuckets.lib.util.HasLifecycle;

import java.util.List;
import java.util.Map;

public class AutoPathInstance implements HasLifecycle {

    final List<PathPlannerTrajectory> multiTrajectory;
    final Map<String, Double> eventToTimeMap;
    final List<Record> records; //needs to be inserted highest back
    final AutoPath type;

    static class Record {
        final double timestamp;
        final int index;

        public Record(double timestamp, int index) {
            this.timestamp = timestamp;
            this.index = index;
        }
    }

    class Results {
        final int index;
        final double timestamp;

        public Results(int index, double timestamp) {
            this.index = index;
            this.timestamp = timestamp;
        }
    }

    public AutoPathInstance(List<PathPlannerTrajectory> multiTrajectory, Map<String, Double> eventToTimeMap, List<Record> records, AutoPath type) {
        this.multiTrajectory = multiTrajectory;
        this.eventToTimeMap = eventToTimeMap;
        this.records = records;
        this.type = type;
    }

    //this is dumb

    Timer totalPathTimer = new Timer();

    public Results lastTimestampStamp(double currentTime) {
        for (Record record : records) { //latest timestamp must come first
            if (currentTime > record.timestamp) {
                return new Results(record.index, record.timestamp);
            }
        }

        return new Results(0, 0);
    }


    /**
     * @param eventName the event of the path
     * @return whether it has happened yet
     */
    public boolean sampleHasEventStarted(String eventName) {
        if (type == AutoPath.NONE) {
            return false;
        }

        double secondsNow = totalPathTimer.get();
        if (eventToTimeMap.get(eventName) == null) return false;

        return secondsNow > eventToTimeMap.get(eventName);
    }


    public PathPlannerTrajectory.PathPlannerState sampleSpeeds() {
        if (type == AutoPath.NONE) {
            return new PathPlannerTrajectory.PathPlannerState();
        }

        double secondsNow = totalPathTimer.get();
        Results lastTimestamp = lastTimestampStamp(secondsNow);
        double secondsDelta = secondsNow - lastTimestamp.timestamp;


        return (PathPlannerTrajectory.PathPlannerState) multiTrajectory.get(lastTimestamp.index).sample(secondsDelta);
    }

    public boolean isDone() {
        return totalPathTimer.hasElapsed(totalPathTimer.get());
    }


    @Override
    public void start() {
        totalPathTimer.start();
    }

    @Override
    public void end() {
        totalPathTimer.stop();
    }


}
