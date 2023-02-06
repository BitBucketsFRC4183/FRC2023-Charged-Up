package org.bitbuckets.odometry;

import com.ctre.phoenix.sensors.WPI_PigeonIMU;
import edu.wpi.first.math.estimator.SwerveDrivePoseEstimator;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.util.WPIUtilJNI;
import org.bitbuckets.drive.IDriveControl;
import org.bitbuckets.lib.log.ILoggable;
import org.bitbuckets.vision.PhotonCalculationResult;
import org.bitbuckets.vision.VisionControl;

import java.util.Optional;

public class OdometryControl implements IOdometryControl, Runnable {

    final IDriveControl driveControl;

    final WPI_PigeonIMU pigeonIMU;
    final SwerveDrivePoseEstimator swerveDrivePoseEstimator;
    final VisionControl visionControl;

    final ILoggable<Pose3d> robotPoseLog;
    final ILoggable<Double> gyroAngleLog;
    final ILoggable<Pose2d> estimatedPose2dLog;

    public OdometryControl(IDriveControl driveControl, SwerveDrivePoseEstimator swerveDrivePoseEstimator, WPI_PigeonIMU pigeonIMU, VisionControl visionControl, ILoggable<Pose3d> robotPoseLog, ILoggable<Double> gyroAngleLog, ILoggable<Pose2d> estimatedPose2dLog) {
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

        Optional<PhotonCalculationResult> res = visionControl.visionPoseEstimator();

        if (res.isPresent()) {
            Pose2d realPose = res.get().robotPose.toPose2d();

            swerveDrivePoseEstimator.addVisionMeasurement(realPose, epoch);
            robotPoseLog.log(res.get().robotPose);
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
