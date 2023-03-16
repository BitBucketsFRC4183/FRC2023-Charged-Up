package org.bitbuckets.drive.holo;

import com.pathplanner.lib.PathPlannerTrajectory;
import edu.wpi.first.math.controller.HolonomicDriveController;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import org.bitbuckets.drive.IDriveControl;
import org.bitbuckets.lib.log.IDebuggable;
import org.bitbuckets.odometry.IOdometryControl;

public class HoloControl {

    final IDriveControl driveControl;

    final IOdometryControl odometryControl;
    final HolonomicDriveController controller;

    final IDebuggable debuggable;

    public HoloControl(IDriveControl driveControl, IOdometryControl odometryControl, HolonomicDriveController controller, IDebuggable debuggable) {
        this.driveControl = driveControl;
        this.odometryControl = odometryControl;
        this.controller = controller;
        this.debuggable = debuggable;
    }


    //TODO this is dangerous

    /**
     * @param target setpoint global
     */
    public ChassisSpeeds calculatePose2D(Pose2d target, double desiredVelocity) {


        var speed = controller.calculate(
                odometryControl.estimateFusedPose2d(),
                target,
                desiredVelocity,
                target.getRotation()



        );
        debuggable.log("last-target", target);

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

}
