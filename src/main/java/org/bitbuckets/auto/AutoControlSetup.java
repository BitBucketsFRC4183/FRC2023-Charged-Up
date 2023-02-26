package org.bitbuckets.auto;

import com.pathplanner.lib.PathConstraints;
import com.pathplanner.lib.PathPlanner;
import com.pathplanner.lib.PathPlannerTrajectory;
import org.bitbuckets.lib.IProcess;
import org.bitbuckets.lib.ISetup;

import java.util.ArrayList;
import java.util.List;

public class AutoControlSetup implements ISetup<IAutoControl> {


    @Override
    public IAutoControl build(IProcess self) {


        List<List<PathPlannerTrajectory>> paths = new ArrayList<>();
        try {


            List<PathPlannerTrajectory> trajectory = PathPlanner.loadPathGroup("test-forwardright", new PathConstraints(1.25, 2.25), new PathConstraints(1.00, 1));
            List<PathPlannerTrajectory> trajectory1 = PathPlanner.loadPathGroup("taxi-right", new PathConstraints(4.0, 3.0));
            List<PathPlannerTrajectory> trajectory2 = PathPlanner.loadPathGroup("taxi-left", new PathConstraints(4.0, 3.0));
            List<PathPlannerTrajectory> trajectory3 = PathPlanner.loadPathGroup("taxi-middle-dock", new PathConstraints(4.0, 3.0));
            List<PathPlannerTrajectory> trajectory4 = PathPlanner.loadPathGroup("taxi-middle-dock-alt", new PathConstraints(4.0, 3.0));
            List<PathPlannerTrajectory> trajectory5 = PathPlanner.loadPathGroup("SC1-CL1-BL", new PathConstraints(4.0, 3.0));
            List<PathPlannerTrajectory> trajectory6 = PathPlanner.loadPathGroup("SC1-CL1-SC3-BL", new PathConstraints(4.0, 3.0));
            List<PathPlannerTrajectory> trajectory8 = PathPlanner.loadPathGroup("SC9-CL4-BL", new PathConstraints(4.0, 3.0));
            List<PathPlannerTrajectory> trajectory9 = PathPlanner.loadPathGroup("SC9-CL4-SC7-BL", new PathConstraints(4.0, 3.0));


            paths.add(trajectory);
            paths.add(trajectory1);
            paths.add(trajectory2);
            paths.add(trajectory3);
            paths.add(trajectory4);
            paths.add(trajectory5);
            paths.add(trajectory6);
            paths.add(trajectory8);
            paths.add(trajectory9);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }



        return new AutoControl(paths);
    }
}
