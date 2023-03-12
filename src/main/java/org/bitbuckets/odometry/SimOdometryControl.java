package org.bitbuckets.odometry;

import edu.wpi.first.math.estimator.SwerveDrivePoseEstimator;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import org.bitbuckets.drive.IDriveControl;
import org.bitbuckets.lib.core.HasLoop;
import org.bitbuckets.lib.debug.IDebuggable;
import org.bitbuckets.lib.log.ILoggable;

public class SimOdometryControl implements IOdometryControl, HasLoop {

    final SwerveDriveKinematics kinematics;
    final SwerveDrivePoseEstimator estimator;
    final IDriveControl driveControl;
    final ILoggable<Pose2d> odoEstimatedPose;
    final IDebuggable debuggable;

    public SimOdometryControl(SwerveDriveKinematics kinematics, SwerveDrivePoseEstimator estimator, IDriveControl driveControl, ILoggable<Pose2d> odoEstimatedPose, IDebuggable debuggable) {
        this.kinematics = kinematics;
        this.estimator = estimator;
        this.driveControl = driveControl;
        this.odoEstimatedPose = odoEstimatedPose;
        this.debuggable = debuggable;
    }

    @Override
    public Pose2d estimateFusedPose2d() {
        return estimator.getEstimatedPosition();
    }

    @Override
    public Rotation2d getRotation2d() {
        return estimator.getEstimatedPosition().getRotation();
    }

    @Override
    public double getYaw_deg() {
        return estimator.getEstimatedPosition().getRotation().getDegrees();
    }

    @Override
    public double getPitch_deg() {
        return 0;
    }

    @Override
    public double getRoll_deg() {
        return 0;
    }

    @Override
    public void zero() {
        //reset
        lastAngle_fieldRelative = Rotation2d.fromDegrees(0);
        estimator.resetPosition(Rotation2d.fromDegrees(0), driveControl.currentPositions(), new Pose2d());
    }

    @Override
    public void setPos(Rotation2d gyroAngle, Pose2d poseMeters) {
        lastAngle_fieldRelative = gyroAngle;
        estimator.resetPosition(gyroAngle, driveControl.currentPositions(), poseMeters);
    }

    Rotation2d lastAngle_fieldRelative = Rotation2d.fromDegrees(0);
    SwerveModulePosition[] lastPositions = new SwerveModulePosition[] {
            new SwerveModulePosition(),
            new SwerveModulePosition(),
            new SwerveModulePosition(),
            new SwerveModulePosition()
    };

    static SwerveModulePosition[] delta(SwerveModulePosition[] now, SwerveModulePosition[] last) {
        SwerveModulePosition[] positions = new SwerveModulePosition[now.length];
        for (int i = 0; i < now.length; i++) {
            positions[i] = new SwerveModulePosition(now[i].distanceMeters - last[i].distanceMeters, now[i].angle);
        }

        return positions;
    }

    @Override
    public void loop() {
        var currentPositions = driveControl.currentPositions();
        var deltaPositions = delta(currentPositions, lastPositions);

        Rotation2d dTheta = new Rotation2d(kinematics.toTwist2d(deltaPositions).dtheta);

        lastAngle_fieldRelative = lastAngle_fieldRelative.plus(dTheta);
        lastPositions = currentPositions;

        debuggable.log("dTheta", dTheta.getDegrees());
        debuggable.log("theta", lastAngle_fieldRelative.getDegrees());


        estimator.update(lastAngle_fieldRelative, currentPositions);
        odoEstimatedPose.log(estimateFusedPose2d());


    }
}
