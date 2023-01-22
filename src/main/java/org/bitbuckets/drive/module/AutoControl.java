package org.bitbuckets.drive.module;

import com.pathplanner.lib.PathPlannerTrajectory;
import edu.wpi.first.math.controller.HolonomicDriveController;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import java.util.function.Supplier;

public class AutoControl {
    private final PathPlannerTrajectory m_trajectory;
    private final Supplier<Pose2d> m_pose;
    private final SwerveDriveKinematics m_kinematics;
    private final HolonomicDriveController m_controller;

    public AutoControl(PathPlannerTrajectory m_trajectory, Supplier<Pose2d> m_pose, SwerveDriveKinematics m_kinematics, HolonomicDriveController m_controller) {
        this.m_trajectory = m_trajectory;
        this.m_pose = m_pose;
        this.m_kinematics = m_kinematics;
        this.m_controller = m_controller;
    }

    public SwerveModuleState[] getAutoStates (double time) {

        var desiredState = (PathPlannerTrajectory.PathPlannerState) m_trajectory.sample(time);

        var targetChassisSpeeds = m_controller.calculate(m_pose.get(), desiredState, desiredState.holonomicRotation);
        var targetModuleStates = m_kinematics.toSwerveModuleStates(targetChassisSpeeds);

        SmartDashboard.putString("/drivetrain/desiredState", desiredState.toString());
        System.out.println("Desired State: " + desiredState.toString());

        SmartDashboard.putNumber("/drivetrain/desired_X", desiredState.poseMeters.getX());
        SmartDashboard.putNumber("/drivetrain/desired_Y", desiredState.poseMeters.getY());
        SmartDashboard.putNumber("/drivetrain/desired_Theta", desiredState.poseMeters.getRotation().getRadians());


        //PathPlannerTrajectory testPath = PathPlanner.loadPath("test path", new PathConstraints(1,1));

        return targetModuleStates;
    }
}
