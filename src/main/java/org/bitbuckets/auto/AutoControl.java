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
            return new AutoPathInstance(new ArrayList<>(), new HashMap<>(), new ArrayList<>(), whichOne);
        }

        var trajectoryGroup = trajectories.get(whichOne.index);

        Map<String, Double> eventMap = new HashMap<>();

        double internalTime = 0;
        List<AutoPathInstance.Record> records = new ArrayList<>();

        for (int i = 0; i < trajectoryGroup.size(); i++) {
            PathPlannerTrajectory traj = trajectoryGroup.get(i);
            internalTime = internalTime + traj.getTotalTimeSeconds();

            for (PathPlannerTrajectory.EventMarker marker : traj.getMarkers()) {
                for (String name : marker.names) {
                    eventMap.put(name, internalTime + marker.timeSeconds);
                }
            }

            records.add(new AutoPathInstance.Record(internalTime, i));
        }

        odometryControl.setPos(trajectoryGroup.get(0).getInitialState().holonomicRotation, swerveModulePositions, trajectoryGroup.get(0).getInitialState().poseMeters);
        AutoPathInstance instance = new AutoPathInstance(trajectoryGroup, eventMap, records, whichOne);

        instance.start();
        return instance;
    }
}
