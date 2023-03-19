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

    public OdometryControl(Vector<N3> visionMeasurementStdDevs, SwerveDrivePoseEstimator swerveDrivePoseEstimator, IDriveControl driveControl, IVisionControl visionControl, IGyro gyro, ILoggable<Pose2d> odoEstimatedPose, ILoggable<Pose2d> visionEstimatedPose) {
        this.visionMeasurementStdDevs = visionMeasurementStdDevs;
        this.swerveDrivePoseEstimator = swerveDrivePoseEstimator;
        this.driveControl = driveControl;
        this.visionControl = visionControl;
        this.gyro = gyro;

        this.odoEstimatedPose = odoEstimatedPose;
        this.visionEstimatedPose = visionEstimatedPose;
    }

    @Override
    public void loop() {
        Rotation2d gyroangle = gyro.getRotation2d();

        Pose2d estimatedPose = swerveDrivePoseEstimator.update(gyroangle, driveControl.currentPositions());
        odoEstimatedPose.log(estimatedPose);

        Optional<Pose3d> res = visionControl.estimateVisionRobotPose();
        if (res.isEmpty()) return;


        double epoch = Timer.getFPGATimestamp();
        Pose2d visionEstimatedPose = res.get().toPose2d();
        this.visionEstimatedPose.log(visionEstimatedPose);
        //swerveDrivePoseEstimator.addVisionMeasurement(visionEstimatedPose, epoch, visionMeasurementStdDevs);
    }

    @Override
    public Pose2d estimateFusedPose2d() {
        return swerveDrivePoseEstimator.getEstimatedPosition();
    }

    @Override
    public Rotation2d getRotation2d() {
        return swerveDrivePoseEstimator.getEstimatedPosition().getRotation();
    }

    @Override
    public double getYaw_deg() {
        return gyro.getYaw_deg();
    }

    @Override
    public double getPitch_deg() {
        return gyro.getPitch_deg();
    }

    @Override
    public double getRoll_deg() {
        return gyro.getRoll_deg();
    }

    @Override
    public double getAccelerationZ() {
        return gyro.getAccelerationZ();
    }

    @Override
    public void zeroOdo() {
        this.swerveDrivePoseEstimator.resetPosition(gyro.getRotation2d(), driveControl.currentPositions(), new Pose2d());
    }

    @Override
    public void zeroGyro() {
        this.gyro.zero();
    }

    @Override
    public void setPos(Pose2d poseMeters, Rotation2d holonomicRotation) {
        this.swerveDrivePoseEstimator.resetPosition(gyro.getRotation2d(), driveControl.currentPositions(), new Pose2d(poseMeters.getTranslation(), holonomicRotation));
    }
}
