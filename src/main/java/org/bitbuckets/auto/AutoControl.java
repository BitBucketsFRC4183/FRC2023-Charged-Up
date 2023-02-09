package org.bitbuckets.auto;

import com.pathplanner.lib.PathPlannerTrajectory;

import java.util.HashMap;
import java.util.Map;

public class AutoControl implements IAutoControl {

    final PathPlannerTrajectory[] trajectories;

    public AutoControl(PathPlannerTrajectory[] trajectories) {
        this.trajectories = trajectories;
    }

    @Override
    public AutoPathInstance generateAndStartPath(AutoPath whichOne) {
        var tj = trajectories[whichOne.index];
        double trajectoryTime = tj.getTotalTimeSeconds();
        Map<String, Double> eventMap = new HashMap<>();
        System.out.println("HERE");

        for (PathPlannerTrajectory.EventMarker marker : tj.getMarkers()) {
            System.out.println("HEREQWERTYUI");

            //TODO all event markers need to have unique names. If they don't this code here will break.
            for (String name : marker.names) {

                System.out.println("SHIT: " + name);
                eventMap.put(name, marker.timeSeconds);
            }
        }

        AutoPathInstance instance = new AutoPathInstance(tj, eventMap, trajectoryTime, whichOne);
        instance.start();
        return instance;
    }
}
