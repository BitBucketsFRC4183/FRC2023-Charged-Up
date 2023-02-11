package org.bitbuckets.drive.holo;

import edu.wpi.first.math.controller.HolonomicDriveController;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.trajectory.Trajectory;
import edu.wpi.first.math.trajectory.TrapezoidProfile;
import org.bitbuckets.drive.controlsds.DriveControl;
import org.bitbuckets.lib.log.Debuggable;
import org.bitbuckets.odometry.IOdometryControl;
import org.bitbuckets.vision.IVisionControl;

public class HoloControl {

    final DriveControl driveControl;

    final IVisionControl visionControl;
    final IOdometryControl odometryControl;
    HolonomicDriveController controller;

    final Debuggable debuggable;

    public HoloControl(DriveControl driveControl, IVisionControl visionControl, IOdometryControl odometryControl, HolonomicDriveController controller, Debuggable debuggable) {
        this.driveControl = driveControl;
        this.visionControl = visionControl;
        this.odometryControl = odometryControl;
        this.controller = controller;
        this.debuggable = debuggable;
    }


    //TODO this is dangerous

    /**
     * @param target setpoint global
     */
    public ChassisSpeeds calculatePose2D(Pose2d target, double desiredVelocity, Rotation2d desiredRotation) {


        var speed = controller.calculate(
                odometryControl.estimateFusedPose2d(),
                target,
                desiredVelocity,
                desiredRotation



        );
        double X_error = controller.getXController().getPositionError();
        double Y_error =  controller.getYController().getPositionError();
        double theta_error = controller.getThetaController().getPositionError();




        if((X_error < 0.4 && X_error > -0.4) && (Y_error < 0.2 && Y_error > -0.2) && (theta_error < 5 && theta_error > -5))
        {
            return new ChassisSpeeds(0,0,0);
        }
        debuggable.log("rotation", speed.omegaRadiansPerSecond);
        debuggable.log("x-movement", speed.vxMetersPerSecond);
        debuggable.log("y-movement", speed.vyMetersPerSecond);
    return speed;
    }

    public ChassisSpeeds calculatePose2DFromState(Trajectory.State state) {
        return controller.calculate(
                odometryControl.estimateFusedPose2d(),
                state,
                state.poseMeters.getRotation()
        );
    }
}
