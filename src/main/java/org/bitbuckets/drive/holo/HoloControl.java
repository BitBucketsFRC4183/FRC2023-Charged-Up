package org.bitbuckets.drive.holo;

import com.pathplanner.lib.PathPlannerTrajectory;
import edu.wpi.first.math.controller.HolonomicDriveController;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import org.bitbuckets.drive.controlsds.DriveControl;
import org.bitbuckets.odometry.IOdometryControl;
import org.bitbuckets.vision.IVisionControl;
import org.bitbuckets.vision.VisionControl;

public class HoloControl {

    final DriveControl driveControl;

    final IVisionControl visionControl;
    final IOdometryControl odometryControl;
    final HolonomicDriveController controller;

    public HoloControl(DriveControl driveControl, IVisionControl visionControl, IOdometryControl odometryControl, HolonomicDriveController controller) {
        this.driveControl = driveControl;
        this.visionControl = visionControl;
        this.odometryControl = odometryControl;
        this.controller = controller;
    }


    //TODO this is dangerous

    /**
     * @param target setpoint global
     */
    public ChassisSpeeds calculatePose2D(Pose2d target, double desiredVelocity, Rotation2d desiredRotation) {
        return controller.calculate(
                visionControl.estimateRobotPose().get().toPose2d(),
                target,
                desiredVelocity,
                desiredRotation

        );
    }

    public ChassisSpeeds calculatePose2DFromState(PathPlannerTrajectory.PathPlannerState state) {
        return controller.calculate(
                odometryControl.estimatePose2d(),
                state,
                state.holonomicRotation
        );
    }
}
