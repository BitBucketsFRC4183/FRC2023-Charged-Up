package org.bitbuckets.odometry;

import edu.wpi.first.math.estimator.SwerveDrivePoseEstimator;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import org.bitbuckets.drive.IDriveControl;
import org.bitbuckets.lib.core.HasLoop;
import org.bitbuckets.lib.hardware.IGyro;
import org.bitbuckets.lib.log.IDebuggable;
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
    public Pose2d estimatePose_trueFieldPose() {
        return estimator.getEstimatedPosition();
    }

    @Override
    public IGyro getGyro() {
        throw new UnsupportedOperationException();
    }


    @Override
    public void zero() {
        //reset
        estimator.resetPosition(Rotation2d.fromDegrees(0), driveControl.currentPositions_initializationRelative(), new Pose2d());
    }

    @Override
    public void setPos(Pose2d pose_trueFieldRelative) {
        estimator.resetPosition(pose_trueFieldRelative.getRotation(), driveControl.currentPositions_initializationRelative(), pose_trueFieldRelative);
    }

    Rotation2d lastAngle_initializationRelative = Rotation2d.fromDegrees(0);
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
        var currentPositions = driveControl.currentPositions_initializationRelative();
        var deltaPositions = delta(currentPositions, lastPositions);

        Rotation2d dTheta = new Rotation2d(kinematics.toTwist2d(deltaPositions).dtheta);

        lastAngle_initializationRelative = lastAngle_initializationRelative.plus(dTheta);
        lastPositions = currentPositions;

        debuggable.log("dTheta", dTheta.getDegrees());
        debuggable.log("theta", lastAngle_initializationRelative.getDegrees());


        estimator.update(lastAngle_initializationRelative, currentPositions);
        odoEstimatedPose.log(estimatePose_trueFieldPose());




    }
}
