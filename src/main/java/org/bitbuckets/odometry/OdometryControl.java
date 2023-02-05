package org.bitbuckets.odometry;

import com.ctre.phoenix.sensors.WPI_PigeonIMU;
import edu.wpi.first.math.estimator.SwerveDrivePoseEstimator;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import org.bitbuckets.drive.IDriveControl;
import org.bitbuckets.vision.VisionControl;

public class OdometryControl implements IOdometryControl, Runnable {

    final IDriveControl driveControl;
    final VisionControl visionControl;
    final WPI_PigeonIMU pigeonIMU;
    final SwerveDrivePoseEstimator swerveDrivePoseEstimator;

    public OdometryControl(IDriveControl driveControl, VisionControl visionControl, WPI_PigeonIMU pigeonIMU, SwerveDrivePoseEstimator swerveDrivePoseEstimator) {
        this.driveControl = driveControl;
        this.visionControl = visionControl;
        this.pigeonIMU = pigeonIMU;
        this.swerveDrivePoseEstimator = swerveDrivePoseEstimator;
    }


    @Override
    public void run() {
        Rotation2d gyroangle = pigeonIMU.getRotation2d();
        swerveDrivePoseEstimator.update(gyroangle, driveControl.currentPositions());

        //TODO log gyro
    }


    @Override
    public Pose2d estimatePose2d() {
        return swerveDrivePoseEstimator.getEstimatedPosition();
    }

    @Override
    public Rotation2d getRotation2d() {
        return pigeonIMU.getRotation2d();
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
