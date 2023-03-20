package org.bitbuckets.drive.holo;

import com.pathplanner.lib.PathPlannerTrajectory;
import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.util.Units;
import org.bitbuckets.lib.control.IPIDCalculator;
import org.bitbuckets.lib.core.HasLifecycle;
import org.bitbuckets.lib.log.IDebuggable;
import org.bitbuckets.odometry.IOdometryControl;

public class WorseHoloControl implements HasLifecycle {

    final IPIDCalculator xPid;
    final IPIDCalculator yPid;
    final IPIDCalculator thetaPid_radians;

    final IOdometryControl odometryControl;

    final IDebuggable debuggable;

    public WorseHoloControl(IPIDCalculator xPid, IPIDCalculator yPid, IPIDCalculator thetaPid_radians, IOdometryControl odometryControl, IDebuggable debuggable) {
        this.xPid = xPid;
        this.yPid = yPid;
        this.thetaPid_radians = thetaPid_radians;
        this.odometryControl = odometryControl;
        this.debuggable = debuggable;
    }

    public ChassisSpeeds find(PathPlannerTrajectory.PathPlannerState state) {

        //This is our current pose
        Pose2d currentPose_trueFieldRelative = odometryControl.estimatePose_trueFieldPose();

        Pose2d desiredPoseWithWeirdRotation_trueFieldRelative = state.poseMeters;
        Pose2d desiredPose_trueFieldRelative = new Pose2d(
                desiredPoseWithWeirdRotation_trueFieldRelative.getTranslation(),
                state.holonomicRotation
        );

        //TODO why do we multiply by the current rotation?
        double xFF = state.velocityMetersPerSecond * currentPose_trueFieldRelative.getRotation().getCos();
        double yFF = state.velocityMetersPerSecond * currentPose_trueFieldRelative.getRotation().getSin();


        //TODO do we consider holonomics here?
        double thetaFeedback = thetaPid_radians.calculateNext(
                currentPose_trueFieldRelative.getRotation().getRadians(),
                state.poseMeters.getRotation().getRadians() //TODO there has to be a reason we do this and ignore holonomic..?
                //wait... we use holonomic only to set one thing!
        );


        // Calculate feedback velocities (based on position error).

        debuggable.log("current-feedback", currentPose_trueFieldRelative.getRotation().getDegrees());
        debuggable.log("desired-feedback", state.poseMeters.getRotation().getDegrees());
        debuggable.log("output-feedback", thetaFeedback);

        double xFeedback = xPid.calculateNext(
                currentPose_trueFieldRelative.getX(),
                desiredPose_trueFieldRelative.getX()
        );
        double yFeedback = yPid.calculateNext(
                currentPose_trueFieldRelative.getY(),
                desiredPose_trueFieldRelative.getY()
        );


        return new ChassisSpeeds(
          xFF +xFeedback,
          yFF + yFeedback,
          thetaFeedback
        );
        /*return new ChassisSpeeds(
            (xFF + xFeedback) * robotAngle.getCos() + (yFF + yFeedback) * robotAngle.getSin(),
            -(xFF + xFeedback) * robotAngle.getSin() + (yFF + yFeedback) * robotAngle.getCos(),
                thetaFeedback);*/
    }



    @Override
    public void autonomousInit() {
        //thetaPid_radians.rawAccess(ProfiledPIDController.class).reset(0);
    }
}
