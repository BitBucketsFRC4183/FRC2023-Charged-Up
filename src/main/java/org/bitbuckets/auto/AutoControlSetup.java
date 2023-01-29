package org.bitbuckets.auto;

import com.pathplanner.lib.PathConstraints;
import com.pathplanner.lib.PathPlanner;
import com.pathplanner.lib.PathPlannerTrajectory;
import edu.wpi.first.math.controller.HolonomicDriveController;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.trajectory.TrapezoidProfile;
import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.ProcessPath;
import org.bitbuckets.lib.SetupProfiler;

/**
 * labels: low priority, easy
 * TODO: This should use {@link org.bitbuckets.lib.control.IPIDCalculator instead of PidController when it is done}
 */
public class AutoControlSetup implements ISetup<AutoControl> {


    @Override
    public AutoControl build(ProcessPath path) {
        SetupProfiler load = path.generateSetupProfiler("load-auto-paths");
        SetupProfiler gen = path.generateSetupProfiler("generate-objects");

        load.markProcessing();
        //load paths
        PathPlannerTrajectory trajectory = PathPlanner.loadPath("test path #1", new PathConstraints(4.0, 3.0));
        PathPlannerTrajectory trajectory1 = PathPlanner.loadPath("leave community #2", new PathConstraints(4.0, 3.0));
        PathPlannerTrajectory trajectory2 = PathPlanner.loadPath("score 2 GP #3", new PathConstraints(4.0, 3.0));
        PathPlannerTrajectory trajectory3 = PathPlanner.loadPath("score 3 GP #4", new PathConstraints(4.0, 3.0));
        PathPlannerTrajectory trajectory4 = PathPlanner.loadPath("score 1 + balance #5", new PathConstraints(4.0, 3.0));
        PathPlannerTrajectory trajectory5 = PathPlanner.loadPath("2GP + balance #6", new PathConstraints(4.0, 3.0));
        PathPlannerTrajectory trajectory6 = PathPlanner.loadPath("score 1 + collect 1 + balance #7", new PathConstraints(4.0, 3.0));
        PathPlannerTrajectory[] traj = new PathPlannerTrajectory[]{
                trajectory, trajectory1, trajectory2, trajectory3, trajectory4, trajectory5, trajectory6
        };

        load.markCompleted();

        gen.markProcessing();


        var controller = new HolonomicDriveController(
                new PIDController(
                        AutoConstants.pathXYPID[0],
                        AutoConstants.pathXYPID[1],
                        AutoConstants.pathXYPID[2]
                ),
                new PIDController(
                        AutoConstants.pathXYPID[0],
                        AutoConstants.pathXYPID[1],
                        AutoConstants.pathXYPID[2]
                ),
                new ProfiledPIDController(
                        AutoConstants.pathThetaPID[0],
                        AutoConstants.pathThetaPID[1],
                        AutoConstants.pathThetaPID[2],
                        new TrapezoidProfile.Constraints(AutoConstants.maxPathFollowVelocity, AutoConstants.maxPathFollowAcceleration)
                )
        );
        gen.markCompleted();

        return new AutoControl(traj, controller);
    }


}
