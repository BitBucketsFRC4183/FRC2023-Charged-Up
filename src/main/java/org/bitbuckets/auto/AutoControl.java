package org.bitbuckets.auto;

import com.pathplanner.lib.PathPlannerTrajectory;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.wpilibj.DriverStation;
import org.bitbuckets.odometry.IOdometryControl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class AutoControl implements IAutoControl {

    final List<List<PathPlannerTrajectory>> trajectories;
    final IOdometryControl odometryControl;

    public AutoControl(List<List<PathPlannerTrajectory>> trajectories, IOdometryControl odometryControl) {
        this.trajectories = trajectories;
        this.odometryControl = odometryControl;
    }

    @Override
    public AutoPathInstance generateAndStartPath(AutoPath whichOne) {
        if (whichOne == AutoPath.NONE) {
            return new AutoPathInstance(new ArrayList<>(), new HashMap<>(), new ArrayList<>(), whichOne, 0);
        }

        var trajectoryGroup = trajectories.get(whichOne.index);

        Map<String, Double> eventMap = new HashMap<>();

        double totalTime = 0;
        List<AutoPathInstance.SegmentTime> segmentTimes = new ArrayList<>();



        for (int i = 0; i < trajectoryGroup.size(); i++) {
            PathPlannerTrajectory segment = trajectoryGroup.get(i);
            segmentTimes.add(new AutoPathInstance.SegmentTime(i, totalTime));
            totalTime = totalTime + segment.getTotalTimeSeconds();

            //sex time


            for (PathPlannerTrajectory.EventMarker marker : segment.getMarkers()) {
                for (String name : marker.names) {

                    System.out.println(name);
                    eventMap.put(name, totalTime + marker.timeSeconds);
                }
            }

            //add the stop events and wait time

            PathPlannerTrajectory.StopEvent event = segment.getEndStopEvent();

            for (String possibleEventName : event.names) {
                eventMap.put(possibleEventName, totalTime);
            }

            //add wait time
            totalTime = totalTime + segment.getEndStopEvent().waitTime;
        }

        var transformedTrajectories = new ArrayList<PathPlannerTrajectory>();
        for (var trajectory : trajectoryGroup) {
            var transformTrajectory = PathPlannerTrajectory.transformTrajectoryForAlliance(trajectory, DriverStation.getAlliance());
            transformedTrajectories.add(transformTrajectory);
        }



        var initialState = PathPlannerTrajectory.transformStateForAlliance(trajectoryGroup.get(0).getInitialState(), DriverStation.getAlliance());
        odometryControl.setPos(initialState.holonomicRotation, initialState.poseMeters);
        AutoPathInstance instance = new AutoPathInstance(transformedTrajectories, eventMap, segmentTimes, whichOne, totalTime);



        instance.onPhaseChangeEvent(AutoFSM.AUTO_RUN);
        return instance;
    }
}
