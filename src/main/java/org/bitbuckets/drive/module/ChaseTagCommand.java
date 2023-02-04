package org.bitbuckets.drive.module;


import edu.wpi.first.math.geometry.*;
import org.bitbuckets.drive.DriveSubsystem;
import org.bitbuckets.drive.controlsds.DriveControl;

import org.bitbuckets.odometry.OdometryData;
import org.bitbuckets.vision.VisionControl;
import org.photonvision.PhotonCamera;
import org.photonvision.targeting.PhotonPipelineResult;
import org.photonvision.targeting.PhotonTrackedTarget;

import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.trajectory.TrapezoidProfile;
import edu.wpi.first.math.util.Units;


public class ChaseTagCommand {

    private static final TrapezoidProfile.Constraints X_CONSTRAINTS = new TrapezoidProfile.Constraints(1, .75);
    private static final TrapezoidProfile.Constraints Y_CONSTRAINTS = new TrapezoidProfile.Constraints(1, .75);
    private static final TrapezoidProfile.Constraints OMEGA_CONSTRAINTS =   new TrapezoidProfile.Constraints(4, 4);

    private static final int TAG_TO_CHASE = 2;
    private static final Transform3d TAG_TO_GOAL =
            new Transform3d(
                    new Translation3d(1.5, 0.0, 0.0),
                    new Rotation3d(0.0, 0.0, Math.PI));

    //private final Supplier<Pose2d> poseProvider;

    private final DriveControl driveControl;
    final VisionControl visionControl;

    private final ProfiledPIDController xController = new ProfiledPIDController(.5, 0, 0, X_CONSTRAINTS);
    private final ProfiledPIDController yController = new ProfiledPIDController(.5, 0, 0, Y_CONSTRAINTS);
    private final ProfiledPIDController omegaController = new ProfiledPIDController(.5, 0, 0, OMEGA_CONSTRAINTS);

    private PhotonTrackedTarget lastTarget;

    public ChaseTagCommand(DriveControl driveControl, VisionControl visionControl)

    {

        this.driveControl = driveControl;
        this.visionControl = visionControl;

        xController.setTolerance(0.2);
        yController.setTolerance(0.2);
        omegaController.setTolerance(Units.degreesToRadians(3));
        omegaController.enableContinuousInput(0, 2 * Math.PI);

    }


    public void initialize() {
        lastTarget = null;
        Pose2d robotPose = visionControl.visionPoseEstimator().orElseThrow().robotPose.toPose2d();
        omegaController.reset(robotPose.getRotation().getRadians());
        xController.reset(robotPose.getX());
        yController.reset(robotPose.getY());
    }


    public void execute() {
        var robotPose2d = visionControl.visionPoseEstimator().orElseThrow().robotPose.toPose2d();
        var s = visionControl.visionPoseEstimator();
        var robotPose =
                new Pose3d(
                        robotPose2d.getX(),
                        robotPose2d.getY(),
                        0.0,
                        new Rotation3d(0.0, 0.0, robotPose2d.getRotation().getRadians()));

        VisionControl.PhotonCalculationResult res = visionControl.visionPoseEstimator().orElseThrow();
        Pose3d robotPose1 = res.robotPose;
        Translation2d translationToTag = res.translationToTag;
        Pose3d goalPose = res.goalPose;


        if (s.isPresent()) {

            // Drive
            xController.setGoal(goalPose.getX());
            yController.setGoal(goalPose.getY());
            omegaController.setGoal(goalPose.getRotation().toRotation2d().getRadians());
        }





        if (s.isPresent()) {
            var p2 = s.get().robotPose.toPose2d();
            var a = s.get().robotPose;

            if (lastTarget == null) {
                // No target has been visible
                driveControl.stop();
            } else {
                // Drive to the target
                var xSpeed = xController.calculate(p2.getX());
                if (xController.atGoal()) {
                    xSpeed = 0;
                }

                var ySpeed = yController.calculate(p2.getY());
                if (yController.atGoal()) {
                    ySpeed = 0;
                }

                var omegaSpeed = omegaController.calculate(p2.getRotation().getRadians());
                if (omegaController.atGoal()) {
                    omegaSpeed = 0;
                }

                driveControl.drive(
                        ChassisSpeeds.fromFieldRelativeSpeeds(xSpeed, ySpeed, omegaSpeed, p2.getRotation()));
            }
        }



    }


    public void end(boolean interrupted) {
        driveControl.stop();
    }

}