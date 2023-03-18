package org.bitbuckets.auto;

import com.pathplanner.lib.PathConstraints;
import com.pathplanner.lib.PathPlanner;
import com.pathplanner.lib.PathPlannerTrajectory;
import org.bitbuckets.lib.IProcess;
import org.bitbuckets.lib.ISetup;
import org.bitbuckets.odometry.IOdometryControl;

import java.util.ArrayList;
import java.util.List;

public class AutoControlSetup implements ISetup<IAutoControl> {

    final IOdometryControl odometryControl;

    public AutoControlSetup(IOdometryControl odometryControl) {
        this.odometryControl = odometryControl;
    }

    @Override
    public IAutoControl build(IProcess self) {




        List<List<PathPlannerTrajectory>> paths = new ArrayList<>();
        try {

            List<PathPlannerTrajectory> trajectory1 = PathPlanner.loadPathGroup("score-taxi-right", new PathConstraints(3.0, 2.0), new PathConstraints(3.0, 2.0));
            List<PathPlannerTrajectory> trajectory2 = PathPlanner.loadPathGroup("score-taxi-left", new PathConstraints(3.0, 2.0), new PathConstraints(3.0, 2.0));
            List<PathPlannerTrajectory> trajectory3 = PathPlanner.loadPathGroup("score-taxi-mid", new PathConstraints(3.0, 2.0), new PathConstraints(3.0, 2.0));

            paths.add(trajectory1);
            paths.add(trajectory2);
            paths.add(trajectory3);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }


        return new AutoControl(paths, odometryControl, null);
    }
}
