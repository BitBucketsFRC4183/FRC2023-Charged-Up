package org.bitbuckets.drive.module;

import com.pathplanner.lib.PathPlannerTrajectory;
import edu.wpi.first.math.controller.HolonomicDriveController;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;

import java.util.function.Supplier;

public class AutoControl {
    private final PathPlannerTrajectory m_trajectory;
    private final HolonomicDriveController m_controller;

    public AutoControl(PathPlannerTrajectory m_trajectory, HolonomicDriveController m_controller) {
        this.m_trajectory = m_trajectory;
        this.m_controller = m_controller;
    }

    public ChassisSpeeds getAutoChassisSpeeds (double time, Pose2d pose) {

        var desiredState = (PathPlannerTrajectory.PathPlannerState) m_trajectory.sample(time);

        var targetChassisSpeeds = m_controller.calculate(pose, desiredState, desiredState.holonomicRotation);

        return targetChassisSpeeds;
    }
}
