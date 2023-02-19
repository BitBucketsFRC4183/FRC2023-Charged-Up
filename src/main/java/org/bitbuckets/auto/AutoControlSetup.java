package org.bitbuckets.auto;

import com.pathplanner.lib.PathConstraints;
import com.pathplanner.lib.PathPlanner;
import com.pathplanner.lib.PathPlannerTrajectory;
import org.bitbuckets.lib.IProcess;
import org.bitbuckets.lib.ISetup;

public class AutoControlSetup implements ISetup<IAutoControl> {


    @Override
    public IAutoControl build(IProcess self) {

        PathPlannerTrajectory[] traj; //bad
        try {
            PathPlannerTrajectory trajectory = PathPlanner.loadPath("test-forwardright", new PathConstraints(1.25, 2.25));
            PathPlannerTrajectory trajectory1 = PathPlanner.loadPath("taxi-right", new PathConstraints(4.0, 3.0));
            PathPlannerTrajectory trajectory2 = PathPlanner.loadPath("taxi-left", new PathConstraints(4.0, 3.0));
            PathPlannerTrajectory trajectory3 = PathPlanner.loadPath("taxi-middle-dock", new PathConstraints(4.0, 3.0));
            PathPlannerTrajectory trajectory4 = PathPlanner.loadPath("taxi-middle-dock-alt", new PathConstraints(4.0, 3.0));
            PathPlannerTrajectory trajectory5 = PathPlanner.loadPath("SC1-CL1-BL", new PathConstraints(4.0, 3.0));
            PathPlannerTrajectory trajectory6 = PathPlanner.loadPath("SC1-CL1-SC3-BL", new PathConstraints(4.0, 3.0));
            PathPlannerTrajectory trajectory8 = PathPlanner.loadPath("SC9-CL4-BL", new PathConstraints(4.0, 3.0));
            PathPlannerTrajectory trajectory9 = PathPlanner.loadPath("SC9-CL4-SC7-BL", new PathConstraints(4.0, 3.0));
            traj = new PathPlannerTrajectory[]{
                    trajectory, trajectory1, trajectory2, trajectory3, trajectory4, trajectory5, trajectory6, trajectory8, trajectory9
            };
        } catch (Exception e) {
            throw new RuntimeException(e);
        }



        return new AutoControl(traj);
    }
}
