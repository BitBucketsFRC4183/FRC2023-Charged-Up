package org.bitbuckets.odometry;

import edu.wpi.first.math.Vector;
import edu.wpi.first.math.estimator.SwerveDrivePoseEstimator;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.numbers.N3;
import edu.wpi.first.wpilibj.Timer;
import org.bitbuckets.drive.IDriveControl;
import org.bitbuckets.lib.core.HasLoop;
import org.bitbuckets.lib.hardware.IGyro;
import org.bitbuckets.lib.log.IDebuggable;
import org.bitbuckets.lib.log.ILoggable;
import org.bitbuckets.vision.IVisionControl;

import java.util.Optional;

public class OdometryControl implements HasLoop, IOdometryControl {

    final Vector<N3> visionMeasurementStdDevs;
    final SwerveDrivePoseEstimator swerveDrivePoseEstimator;
    final IDriveControl driveControl;
    final IVisionControl visionControl;
    final IGyro gyro;

    final ILoggable<Pose2d> odoEstimatedPose;
    final ILoggable<Pose2d> visionEstimatedPose;
    final IDebuggable debuggable;

    public OdometryControl(Vector<N3> visionMeasurementStdDevs, SwerveDrivePoseEstimator swerveDrivePoseEstimator, IDriveControl driveControl, IVisionControl visionControl, IGyro gyro, ILoggable<Pose2d> odoEstimatedPose, ILoggable<Pose2d> visionEstimatedPose, IDebuggable debuggable) {
        this.visionMeasurementStdDevs = visionMeasurementStdDevs;
        this.swerveDrivePoseEstimator = swerveDrivePoseEstimator;
        this.driveControl = driveControl;
        this.visionControl = visionControl;
        this.gyro = gyro;

        this.odoEstimatedPose = odoEstimatedPose;
        this.visionEstimatedPose = visionEstimatedPose;
        this.debuggable = debuggable;
    }

    @Override
    public void loop() {
        Pose2d estimatedPose = swerveDrivePoseEstimator.update(
                gyro.getRotation2d_initializationRelative(),
                driveControl.currentPositions_initializationRelative()
        );


        odoEstimatedPose.log(estimatedPose);

        Optional<Pose3d> res = visionControl.estimateVisionRobotPose();
        if (res.isEmpty()) return;


        double epoch = Timer.getFPGATimestamp();
        Pose2d visionEstimatedPose = res.get().toPose2d();
        this.visionEstimatedPose.log(visionEstimatedPose);
        //swerveDrivePoseEstimator.addVisionMeasurement(visionEstimatedPose, epoch, visionMeasurementStdDevs);
    }

    @Override
    public Pose2d estimatePose_trueFieldPose() {
        return swerveDrivePoseEstimator.getEstimatedPosition();
    }

    @Override
    public Rotation2d getRotation2d_initializationRelative() {
        return gyro.getRotation2d_initializationRelative();
    }

    @Override
    public double getYaw_deg() {
        return gyro.getAllianceRelativeYaw_deg();
    }

    @Override
    public double getPitch_deg() {
        return gyro.getAllianceRelativePitch_deg();
    }

    @Override
    public double getRoll_deg() {
        return gyro.getAllianceRelativeRoll_deg();
    }

    @Override
    public double getAccelerationZ() {
        return gyro.getAccelerationZ();
    }

    @Override
    public void zero() {

        this.swerveDrivePoseEstimator.resetPosition(
                gyro.getRotation2d_initializationRelative(),
                driveControl.currentPositions_initializationRelative(),
                new Pose2d()
        );
    }

    @Override
    public void setPos(Pose2d pose_trueFieldRelative)
    {


        this.swerveDrivePoseEstimator.resetPosition(
                gyro.getRotation2d_initializationRelative(),
                driveControl.currentPositions_initializationRelative(),
                pose_trueFieldRelative
        );
    }
}
