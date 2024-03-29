package org.bitbuckets.auto;

import com.pathplanner.lib.PathPlannerTrajectory;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.wpilibj.DriverStation;
import org.bitbuckets.lib.log.ILoggable;
import org.bitbuckets.odometry.IOdometryControl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class AutoControl implements IAutoControl {

    final List<List<PathPlannerTrajectory>> trajectories;
    final IOdometryControl odometryControl;

    public AutoControl(List<List<PathPlannerTrajectory>> trajectories, IOdometryControl odometryControl, ILoggable<Pose2d[]> poses) {
        this.trajectories = trajectories;
        this.odometryControl = odometryControl;
        this.poses = poses;
    }


    final ILoggable<Pose2d[]> poses;

    @Override
    public AutoPathInstance generateAndStartPath(AutoPath whichOne) {
        if (whichOne == AutoPath.NONE) {
            return new AutoPathInstance(new ArrayList<>(), new HashMap<>(), new ArrayList<>(), whichOne, 0);
        }

        var segmentGroup = trajectories.get(whichOne.index);


        Map<String, Double> eventMap = new HashMap<>();

        double totalTime = 0;
        List<AutoPathInstance.SegmentTime> segmentTimes = new ArrayList<>();


        for (int i = 0; i < segmentGroup.size(); i++) {
            PathPlannerTrajectory segment = segmentGroup.get(i);

            if (segment.getStartStopEvent().names.size() > 0) {
                segmentTimes.add(new AutoPathInstance.SegmentTime(i, totalTime, true));
                for (String possibleEventName : segment.getStartStopEvent().names) {
                    eventMap.put(possibleEventName, totalTime);
                }
                totalTime += segment.getStartStopEvent().waitTime;
            }

            segmentTimes.add(new AutoPathInstance.SegmentTime(i, totalTime, false));
            totalTime = totalTime + segment.getTotalTimeSeconds();

            //if (segment.getMarkers().size() > 0) throw new UnsupportedOperationException();


            for (PathPlannerTrajectory.EventMarker marker : segment.getMarkers()) {


                for (String name : marker.names) {

                    System.out.println(name + "|" + totalTime + marker.timeSeconds);
                    eventMap.put(name, totalTime + marker.timeSeconds);
                }
            }


            if (segment.getEndStopEvent().names.size() > 0 && segmentGroup.size() - 1 == i) {
                segmentTimes.add(new AutoPathInstance.SegmentTime(i, totalTime, true));

                for (String possibleEventName : segment.getEndStopEvent().names) {
                    eventMap.put(possibleEventName, totalTime);
                }
                totalTime += segment.getEndStopEvent().waitTime;
            }
        }
        var transformedTrajectories = new ArrayList<PathPlannerTrajectory>();
        for (var trajectory : segmentGroup) {
            var transformTrajectory = PathPlannerTrajectory.transformTrajectoryForAlliance(trajectory, DriverStation.getAlliance());
            transformedTrajectories.add(transformTrajectory);
        }


        var initialState = PathPlannerTrajectory.transformStateForAlliance(segmentGroup.get(0).getInitialState(), DriverStation.getAlliance());
        odometryControl.setPos(initialState.poseMeters, initialState.holonomicRotation);
        AutoPathInstance instance = new AutoPathInstance(transformedTrajectories, eventMap, segmentTimes, whichOne, totalTime);

        instance.start();
        return instance;
    }

}
