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
import org.bitbuckets.lib.hardware.PIDIndex;

public class AutoControlSetup implements ISetup<AutoControl> {

    @Override
    public AutoControl build(ProcessPath path) {
        PathPlannerTrajectory trajectory = PathPlanner.loadPath("test path", new PathConstraints(4.0, 3.0));
        var controller = new HolonomicDriveController(
                new PIDController(
                        AutoConstants.pathXYPID[PIDIndex.P],
                        AutoConstants.pathXYPID[PIDIndex.I],
                        AutoConstants.pathXYPID[PIDIndex.D]
                ),
                new PIDController(
                        AutoConstants.pathXYPID[PIDIndex.P],
                        AutoConstants.pathXYPID[PIDIndex.I],
                        AutoConstants.pathXYPID[PIDIndex.D]
                ),
                new ProfiledPIDController(
                        AutoConstants.pathThetaPID[PIDIndex.P],
                        AutoConstants.pathThetaPID[PIDIndex.I],
                        AutoConstants.pathThetaPID[PIDIndex.D],
                        new TrapezoidProfile.Constraints(AutoConstants.maxPathFollowVelocity, AutoConstants.maxPathFollowAcceleration)
                )
        );
        return new AutoControl(trajectory, controller);
    }
}
