package org.bitbuckets.odometry;

import com.ctre.phoenix.sensors.WPI_Pigeon2;
import edu.wpi.first.math.estimator.SwerveDrivePoseEstimator;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.util.WPIUtilJNI;
import org.bitbuckets.drive.IDriveControl;
import org.bitbuckets.lib.log.ILoggable;
import org.bitbuckets.vision.IVisionControl;

import java.util.Optional;

public class OdometryControl implements IOdometryControl, Runnable {


    final IDriveControl driveControl;
    final IVisionControl visionControl;
    final WPI_Pigeon2 pigeonIMU;
    final SwerveDrivePoseEstimator swerveDrivePoseEstimator;

    final ILoggable<Pose3d> robotPoseLog;
    final ILoggable<Double> gyroAngleLog;
    final ILoggable<Pose2d> estimatedPose2dLog;

    public OdometryControl(IDriveControl driveControl, SwerveDrivePoseEstimator swerveDrivePoseEstimator, WPI_Pigeon2 pigeonIMU, IVisionControl visionControl, ILoggable<Pose3d> robotPoseLog, ILoggable<Double> gyroAngleLog, ILoggable<Pose2d> estimatedPose2dLog) {
        this.driveControl = driveControl;
        this.pigeonIMU = pigeonIMU;
        this.swerveDrivePoseEstimator = swerveDrivePoseEstimator;
        this.visionControl = visionControl;
        this.robotPoseLog = robotPoseLog;
        this.gyroAngleLog = gyroAngleLog;
        this.estimatedPose2dLog = estimatedPose2dLog;
    }


    @Override
    public void run() {
        Rotation2d gyroangle = Rotation2d.fromDegrees(pigeonIMU.getAbsoluteCompassHeading());
        double epoch = WPIUtilJNI.now();
        swerveDrivePoseEstimator.updateWithTime(epoch, gyroangle, driveControl.currentPositions());

        Optional<Pose3d> res = visionControl.estimateRobotPose();

        if (res.isPresent()) {
            Pose2d realPose = res.get().toPose2d();

            swerveDrivePoseEstimator.addVisionMeasurement(realPose, epoch);
            robotPoseLog.log(res.get());
        } else {
            robotPoseLog.log(new Pose3d());
        }

        gyroAngleLog.log(gyroangle.getDegrees());
        estimatedPose2dLog.log(swerveDrivePoseEstimator.getEstimatedPosition());

    }

    @Override
    public Pose2d estimatePose2d() {
        return swerveDrivePoseEstimator.getEstimatedPosition();
    }

    @Override
    public Rotation2d getRotation2d() {
        return Rotation2d.fromDegrees(pigeonIMU.getAbsoluteCompassHeading());
    }

    @Override
    public double getYaw_deg() {
        return pigeonIMU.getYaw();
    }

    @Override
    public double getRoll_deg() {
        return pigeonIMU.getRoll();
    }

    @Override
    public void zero() {
        pigeonIMU.reset();
    }


}
