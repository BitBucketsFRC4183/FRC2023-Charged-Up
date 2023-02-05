package org.bitbuckets.odometry;

import com.ctre.phoenix.sensors.WPI_PigeonIMU;
import edu.wpi.first.math.estimator.SwerveDrivePoseEstimator;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.util.WPIUtilJNI;
import org.bitbuckets.drive.IDriveControl;
import org.bitbuckets.vision.VisionControl;

import java.util.Optional;

public class OdometryControl implements IOdometryControl, Runnable {

    final IDriveControl driveControl;
    final VisionControl visionControl;
    final WPI_PigeonIMU pigeonIMU;
    final SwerveDrivePoseEstimator swerveDrivePoseEstimator;

    OdometryControl(IDriveControl driveControl, VisionControl visionControl, WPI_PigeonIMU pigeonIMU, SwerveDrivePoseEstimator swerveDrivePoseEstimator) {
        this.driveControl = driveControl;
        this.visionControl = visionControl;
        this.pigeonIMU = pigeonIMU;
        this.swerveDrivePoseEstimator = swerveDrivePoseEstimator;
    }


    @Override
    public void run() {
        Rotation2d gyroangle = Rotation2d.fromDegrees(pigeonIMU.getAbsoluteCompassHeading());
        double epoch = WPIUtilJNI.now();
        swerveDrivePoseEstimator.updateWithTime(epoch, gyroangle, driveControl.currentPositions());

        Optional<VisionControl.PhotonCalculationResult> res = visionControl.visionPoseEstimator();

        if (res.isPresent()) {
            Pose2d realPose = res.get().robotPose.toPose2d();

            swerveDrivePoseEstimator.addVisionMeasurement(realPose, epoch);
        }

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
