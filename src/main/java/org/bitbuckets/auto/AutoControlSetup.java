package org.bitbuckets.auto;

import com.pathplanner.lib.PathConstraints;
import com.pathplanner.lib.PathPlanner;
import com.pathplanner.lib.PathPlannerTrajectory;
import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.ProcessPath;
import org.bitbuckets.lib.StartupProfiler;

public class AutoControlSetup implements ISetup<IAutoControl> {


    @Override
    public IAutoControl build(ProcessPath self) {
        StartupProfiler load = self.generateSetupProfiler("load-auto-paths");

        load.markProcessing();

        PathPlannerTrajectory[] traj = new PathPlannerTrajectory[0]; //bad
        try {
            PathPlannerTrajectory trajectory = PathPlanner.loadPath("test-path-1", new PathConstraints(1.25, 2.25));
            PathPlannerTrajectory trajectory1 = PathPlanner.loadPath("leave-community-2", new PathConstraints(4.0, 3.0));
            PathPlannerTrajectory trajectory2 = PathPlanner.loadPath("score1-balance-3", new PathConstraints(4.0, 3.0));
            PathPlannerTrajectory trajectory3 = PathPlanner.loadPath("score-3GP-4", new PathConstraints(4.0, 3.0));
            PathPlannerTrajectory trajectory4 = PathPlanner.loadPath("score 1-balance-right-5", new PathConstraints(4.0, 3.0));
            PathPlannerTrajectory trajectory5 = PathPlanner.loadPath("2GP-balance-6", new PathConstraints(4.0, 3.0));
            PathPlannerTrajectory trajectory6 = PathPlanner.loadPath("score1-collect1-balance-7", new PathConstraints(4.0, 3.0));
            traj = new PathPlannerTrajectory[]{
                    trajectory, trajectory1, trajectory2, trajectory3, trajectory4, trajectory5, trajectory6
            };
        } catch (Exception e) {
            load.markErrored(e);
        }
        //load paths


        load.markCompleted();


        return new AutoControl(traj);
    }
}
