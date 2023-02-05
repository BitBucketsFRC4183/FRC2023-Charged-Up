package org.bitbuckets.odometry;

import com.ctre.phoenix.sensors.WPI_PigeonIMU;
import edu.wpi.first.math.estimator.SwerveDrivePoseEstimator;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import org.bitbuckets.drive.DriveConstants;
import org.bitbuckets.drive.IDriveControl;
import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.ProcessPath;
import org.bitbuckets.vision.VisionControl;


public class OdometryControlSetup implements ISetup<OdometryControl> {


    final int id;
    final IDriveControl control;
    final VisionControl visionControl;


    public OdometryControlSetup(int id, IDriveControl control, VisionControl visionControl) {
        this.id = id;
        this.control = control;
        this.visionControl = visionControl;
    }

    @Override
    public OdometryControl build(ProcessPath path) {
        SwerveDrivePoseEstimator estimator = new SwerveDrivePoseEstimator(
                DriveConstants.KINEMATICS,
                Rotation2d.fromDegrees(0),
                new SwerveModulePosition[]{
                        new SwerveModulePosition(),
                        new SwerveModulePosition(),
                        new SwerveModulePosition(),
                        new SwerveModulePosition()
                },
                new Pose2d()
        );

        WPI_PigeonIMU pigeonIMU = new WPI_PigeonIMU(id);

        var robotPoseLog = path.generatePose3dLogger("robot-pose");
        var gyroAngleLog = path.generateDoubleLogger("gyro-ange-degrees");
        var estimatedPose2dLog = path.generatePose2dLogger("estimated-pose");
        OdometryControl odometryControl = new OdometryControl(control, estimator, pigeonIMU, visionControl, robotPoseLog, gyroAngleLog, estimatedPose2dLog);
        ;

        path.registerLoop(odometryControl, "odometry-loop");

        return odometryControl;
    }
}
