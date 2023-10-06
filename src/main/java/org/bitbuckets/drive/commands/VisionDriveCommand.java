package org.bitbuckets.drive.commands;

import edu.wpi.first.math.controller.HolonomicDriveController;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj2.command.CommandBase;
import org.bitbuckets.drive.IDriveControl;
import org.bitbuckets.odometry.IOdometryControl;
import org.bitbuckets.vision.IVisionControl;

public class VisionDriveCommand extends CommandBase {

    final HolonomicDriveController holonomicDriveController;
    final IVisionControl visionControl;
    final IOdometryControl odometryControl;
    final IDriveControl driveControl;
    final double deadband;

    public VisionDriveCommand(HolonomicDriveController holonomicDriveController, IVisionControl visionControl, IOdometryControl odometryControl, IDriveControl driveControl, double deadband) {
        this.holonomicDriveController = holonomicDriveController;
        this.visionControl = visionControl;
        this.odometryControl = odometryControl;
        this.driveControl = driveControl;
        this.deadband = deadband;

        addRequirements(driveControl);
    }

    @Override
    public void execute() {
        var targetPose = visionControl.estimateBestVisionTarget();
        if (targetPose.isEmpty()) return;


        ChassisSpeeds speeds;
        Pose2d target = targetPose.get().toPose2d();

        var desiredSpeeds = holonomicDriveController.calculate(
                odometryControl.estimateFusedPose2d(),
                target,
                1,
                targetPose.get().toPose2d().getRotation().plus(Rotation2d.fromDegrees(180))
        );

        double X_error = holonomicDriveController.getXController().getPositionError();
        double Y_error = holonomicDriveController.getYController().getPositionError();
        double theta_error = holonomicDriveController.getThetaController().getPositionError();

        if ((X_error < 0.4 && X_error > -0.4) && (Y_error < 0.2 && Y_error > -0.2) && (theta_error < 5 && theta_error > -5)) {
            speeds = new ChassisSpeeds(0, 0, 0);
        } else {
            speeds = desiredSpeeds;
        }

        driveControl.drive(speeds);
    }


    @Override
    public boolean isFinished() {
        var poseOpt = visionControl.estimateBestVisionTarget();
        if (poseOpt.isEmpty()) return false;

        var pose = poseOpt.get();
        double xDesired = pose.getX();
        double yDesired = pose.getY();

        var currentPose = odometryControl.estimateFusedPose2d();
        double x = currentPose.getX();
        double y = currentPose.getY();

        //TODO rotatinos

        return (Math.abs(xDesired - x) < deadband) && (Math.abs(yDesired - y) < deadband);

    }

    @Override
    public void end(boolean interrupted) {
        driveControl.stop();
    }
}
