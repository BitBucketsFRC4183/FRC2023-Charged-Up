package org.bitbuckets.auto;

import com.pathplanner.lib.PathPlanner;
import com.pathplanner.lib.PathPlannerTrajectory;
import com.pathplanner.lib.TransformUtil;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.trajectory.Trajectory;
import edu.wpi.first.wpilibj.DriverStation;
import org.bitbuckets.auto.shim.CustomState;
import org.bitbuckets.odometry.IOdometryControl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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

            var transformTrajectory = TransformUtil.transform(trajectory, DriverStation.getAlliance());
            transformedTrajectories.add(transformTrajectory);
        }


        var initialState = transformedTrajectories.get(0).getInitialState();


        odometryControl.setPos(initialState.holonomicRotation, initialState.poseMeters);
        AutoPathInstance instance = new AutoPathInstance(transformedTrajectories, eventMap, segmentTimes, whichOne, totalTime);

        instance.start();
        return instance;
    }

}
