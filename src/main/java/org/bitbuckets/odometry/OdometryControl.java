package org.bitbuckets.odometry;

import edu.wpi.first.math.estimator.SwerveDrivePoseEstimator;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import org.bitbuckets.drive.IDriveControl;
import org.bitbuckets.gyro.GyroControl;
import org.bitbuckets.vision.VisionControl;

public class OdometryControl implements Runnable {

    final IDriveControl driveControl;
    final VisionControl visionControl;
    final GyroControl gyroControl;
    final SwerveDrivePoseEstimator swerveDrivePoseEstimator;

    public OdometryControl(IDriveControl driveControl, VisionControl visionControl, GyroControl gyroControl, SwerveDrivePoseEstimator swerveDrivePoseEstimator) {
        this.driveControl = driveControl;
        this.visionControl = visionControl;
        this.gyroControl = gyroControl;
        this.swerveDrivePoseEstimator = swerveDrivePoseEstimator;
    }


    public Pose2d estimatedSwervePose() {
        return swerveDrivePoseEstimator.getEstimatedPosition();
    }

    @Override
    public void run() {
        Rotation2d gyroangle = gyroControl.getGyroAngle();
        swerveDrivePoseEstimator.update(gyroangle, driveControl.currentPositions());
    }

}
