package org.bitbuckets.drive.holo;

import edu.wpi.first.math.controller.HolonomicDriveController;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.trajectory.Trajectory;
import org.bitbuckets.drive.controlsds.DriveControl;
import org.bitbuckets.odometry.IOdometryControl;

public class HoloControl {

    final DriveControl driveControl;
    final IOdometryControl odometryControl;
    final HolonomicDriveController controller;

    public HoloControl(DriveControl driveControl, IOdometryControl odometryControl, HolonomicDriveController controller) {
        this.driveControl = driveControl;
        this.odometryControl = odometryControl;
        this.controller = controller;
    }


    //TODO this is dangerous
    /**
     *
     * @param pose2d setpoint global
     */
    public ChassisSpeeds calculatePose2D(Pose2d pose2d, double desiredVelocity) {
        return controller.calculate(
                odometryControl.estimatePose2d(),
                pose2d,
                desiredVelocity,
                pose2d.getRotation()
        );
    }

    public ChassisSpeeds calculatePose2DFromState(Trajectory.State state) {
        return controller.calculate(
                odometryControl.estimatePose2d(),
                state,
                state.poseMeters.getRotation()
        );
    }
}
