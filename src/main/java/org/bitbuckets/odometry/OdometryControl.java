package org.bitbuckets.odometry;

import com.ctre.phoenix.sensors.WPI_PigeonIMU;
import edu.wpi.first.math.estimator.SwerveDrivePoseEstimator;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.util.WPIUtilJNI;
import org.bitbuckets.drive.IDriveControl;

public class OdometryControl implements IOdometryControl, Runnable {

    final IDriveControl driveControl;

    final WPI_PigeonIMU pigeonIMU;
    final SwerveDrivePoseEstimator swerveDrivePoseEstimator;

    public OdometryControl(IDriveControl driveControl, SwerveDrivePoseEstimator swerveDrivePoseEstimator, WPI_PigeonIMU pigeonIMU) {
        this.driveControl = driveControl;
        this.pigeonIMU = pigeonIMU;
        this.swerveDrivePoseEstimator = swerveDrivePoseEstimator;
    }


    @Override
    public void run() {
        Rotation2d gyroangle = Rotation2d.fromDegrees(pigeonIMU.getAbsoluteCompassHeading());
        double epoch = WPIUtilJNI.now();
        swerveDrivePoseEstimator.updateWithTime(epoch, gyroangle, driveControl.currentPositions());





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
