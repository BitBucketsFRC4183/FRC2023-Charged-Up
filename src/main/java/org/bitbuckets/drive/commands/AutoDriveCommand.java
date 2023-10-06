package org.bitbuckets.drive.commands;

import com.pathplanner.lib.PathPlannerTrajectory;
import edu.wpi.first.math.controller.HolonomicDriveController;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj2.command.CommandBase;
import org.bitbuckets.auto.AutoPathInstance;
import org.bitbuckets.auto.AutoSubsystem;
import org.bitbuckets.drive.IDriveControl;
import org.bitbuckets.odometry.IOdometryControl;

public class AutoDriveCommand extends CommandBase {

    final HolonomicDriveController holonomicDriveController;
    final AutoSubsystem autoPath;
    final IOdometryControl odometryControl;
    final IDriveControl driveControl;

    public AutoDriveCommand(HolonomicDriveController holonomicDriveController, AutoSubsystem autoPath, IOdometryControl odometryControl, IDriveControl driveControl) {
        this.holonomicDriveController = holonomicDriveController;
        this.autoPath = autoPath;
        this.odometryControl = odometryControl;
        this.driveControl = driveControl;

        addRequirements(driveControl);
    }

    @Override
    public void execute() {
        PathPlannerTrajectory.PathPlannerState state = autoPath.samplePathPlannerState().get();

        ChassisSpeeds speeds = holonomicDriveController.calculate(
                odometryControl.estimateFusedPose2d(),
                state.poseMeters,
                state.velocityMetersPerSecond,
                state.holonomicRotation
        );

        double X_error = holonomicDriveController.getXController().getPositionError();
        double Y_error = holonomicDriveController.getYController().getPositionError();
        double theta_error = holonomicDriveController.getThetaController().getPositionError();

        if ((X_error < 0.4 && X_error > -0.4) && (Y_error < 0.2 && Y_error > -0.2) && (theta_error < 5 && theta_error > -5)) {
            speeds = new ChassisSpeeds(0, 0, 0);
        };

        driveControl.drive(speeds);
    }


    @Override
    public boolean isFinished() {
        return autoPath.isPathDone();
    }

    @Override
    public void end(boolean interrupted) {
        driveControl.stop();
    }
}
