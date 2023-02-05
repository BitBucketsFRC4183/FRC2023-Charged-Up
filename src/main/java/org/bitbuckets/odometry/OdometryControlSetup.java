package org.bitbuckets.odometry;

import com.ctre.phoenix.sensors.Pigeon2;
import com.ctre.phoenix.sensors.WPI_Pigeon2;
import com.ctre.phoenix.sensors.WPI_PigeonIMU;
import edu.wpi.first.math.estimator.SwerveDrivePoseEstimator;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import org.bitbuckets.drive.DriveConstants;
import org.bitbuckets.drive.IDriveControl;
import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.ProcessPath;
import org.bitbuckets.lib.StartupProfiler;
import org.bitbuckets.vision.IVisionControl;
import org.bitbuckets.lib.log.ILoggable;
import org.bitbuckets.lib.log.LoggingConstants;
import org.bitbuckets.vision.VisionControl;

public class OdometryControlSetup implements ISetup<OdometryControl> {


    final int id;
    final IDriveControl control;
    final IVisionControl visionControl;

    public OdometryControlSetup(IDriveControl control, IVisionControl visionControl, int pidgeonId1) {
        this.control = control;
        this.visionControl = visionControl;
        this.pidgeonId = pidgeonId1;
    }

    @Override
    public OdometryControl build(ProcessPath path) {
        StartupProfiler initializePidgeon = addChild.generateSetupProfiler("init-pidgeon");
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

        initializePidgeon.markProcessing();
        WPI_Pigeon2 pigeonIMU = new WPI_Pigeon2(pidgeonId);
        initializePidgeon.markCompleted();
        pigeonIMU.configFactoryDefault();
        pigeonIMU.configMountPose(Pigeon2.AxisDirection.PositiveY, Pigeon2.AxisDirection.PositiveZ);

        OdometryControl odometryControl = new OdometryControl (control, estimator, pigeonIMU, visionControl);;
        addChild.registerLoop(odometryControl, "odometry-loop");
        ILoggable<double[]> loggable = addChild
                .generateDoubleLoggers("yaw", "pitch", "roll", "last-error-code");

        addChild.registerLoop(
                new OdometryLogAspect(pigeonIMU, loggable),
                LoggingConstants.LOGGING_PERIOD,
                "gyro-log"
        );
        addChild.registerLoop(
                odometryControl,
                "odometry-update-loop"
        );

        return odometryControl;
    }
}
