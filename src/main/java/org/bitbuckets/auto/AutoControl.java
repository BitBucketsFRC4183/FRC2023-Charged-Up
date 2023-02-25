package org.bitbuckets.auto;

import com.pathplanner.lib.PathPlannerTrajectory;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import org.bitbuckets.odometry.IOdometryControl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AutoControl implements IAutoControl {

    final List<List<PathPlannerTrajectory>> trajectories;

    public AutoControl(List<List<PathPlannerTrajectory>> trajectories) {
        this.trajectories = trajectories;
    }

    @Override
    public AutoPathInstance generateAndStartPath(AutoPath whichOne, SwerveModulePosition[] swerveModulePositions, IOdometryControl odometryControl) {
        if (whichOne == AutoPath.NONE) {
            return new AutoPathInstance(new ArrayList<>(), new HashMap<>(), new ArrayList<>(), whichOne, 0);
        }

        var trajectoryGroup = trajectories.get(whichOne.index);

        Map<String, Double> eventMap = new HashMap<>();

        double totalTime = 0;
        List<AutoPathInstance.SegmentTime> segmentTimes = new ArrayList<>();

        for (int i = 0; i < trajectoryGroup.size(); i++) {
            PathPlannerTrajectory segment = trajectoryGroup.get(i);
            segmentTimes.add(new AutoPathInstance.SegmentTime(i, totalTime, segment.getTotalTimeSeconds()));
            totalTime = totalTime + segment.getTotalTimeSeconds();

            for (PathPlannerTrajectory.EventMarker marker : segment.getMarkers()) {
                for (String name : marker.names) {
                    eventMap.put(name, totalTime + marker.timeSeconds);
                }
            }

        }

        odometryControl.setPos(trajectoryGroup.get(0).getInitialState().holonomicRotation, swerveModulePositions, trajectoryGroup.get(0).getInitialState().poseMeters);
        AutoPathInstance instance = new AutoPathInstance(trajectoryGroup, eventMap, segmentTimes, whichOne, totalTime);

        instance.start();
        return instance;
    }
}
