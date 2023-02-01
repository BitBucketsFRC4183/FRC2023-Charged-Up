package org.bitbuckets.odometry;

import edu.wpi.first.math.estimator.SwerveDrivePoseEstimator;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import org.bitbuckets.drive.DriveConstants;
import org.bitbuckets.drive.IDriveControl;
import org.bitbuckets.gyro.GyroControl;
import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.ProcessPath;
import org.bitbuckets.vision.VisionControl;

public class OdometryControlSetup implements ISetup<OdometryControl> {


    final IDriveControl control;
    final VisionControl visionControl;
    final GyroControl gyroControl;

    public OdometryControlSetup(IDriveControl control, VisionControl visionControl, GyroControl gyroControl) {
        this.control = control;
        this.visionControl = visionControl;
        this.gyroControl = gyroControl;
    }

    @Override
    public OdometryControl build(ProcessPath addChild) {
        SwerveDrivePoseEstimator estimator = new SwerveDrivePoseEstimator(DriveConstants.KINEMATICS, Rotation2d.fromDegrees(0), new SwerveModulePosition[4], new Pose2d());
        OdometryControl odometryControl = new OdometryControl (control, visionControl, gyroControl, estimator);;

        addChild.registerLoop(odometryControl, "odometry-loop");

        return odometryControl;
    }
}
