package org.bitbuckets.drive.module;

import com.pathplanner.lib.PathPlannerTrajectory;
import edu.wpi.first.math.controller.HolonomicDriveController;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;

public class AutoControl {
    private final PathPlannerTrajectory trajectory;
    private final HolonomicDriveController controller;

    public AutoControl(PathPlannerTrajectory trajectory, HolonomicDriveController controller) {
        this.trajectory = trajectory;
        this.controller = controller;
    }

    public ChassisSpeeds getAutoChassisSpeeds(double time, Pose2d pose) {

        var desiredState = (PathPlannerTrajectory.PathPlannerState) trajectory.sample(time);

        var targetChassisSpeeds = controller.calculate(pose, desiredState, desiredState.holonomicRotation);

        return targetChassisSpeeds;
    }

    public double getTrajectoryTime() {
        return trajectory.getTotalTimeSeconds();
    }
}
