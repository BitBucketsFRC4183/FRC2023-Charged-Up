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
import org.bitbuckets.lib.StartupProfiler;
import org.bitbuckets.vision.IVisionControl;
import org.bitbuckets.vision.VisionControl;

public class OdometryControlSetup implements ISetup<OdometryControl> {


    final IDriveControl control;
    final IVisionControl visionControl;
    final int pidgeonId;

    public OdometryControlSetup(IDriveControl control, IVisionControl visionControl, int pidgeonId1) {
        this.control = control;
        this.visionControl = visionControl;
        this.pidgeonId = pidgeonId1;
    }

    @Override
    public OdometryControl build(ProcessPath addChild) {
        StartupProfiler initializePidgeon = addChild.generateSetupProfiler("init-pidgeon");

        SwerveDrivePoseEstimator estimator = new SwerveDrivePoseEstimator(
                DriveConstants.KINEMATICS,
                Rotation2d.fromDegrees(0),
                new SwerveModulePosition[] {
                        new SwerveModulePosition(),
                        new SwerveModulePosition(),
                        new SwerveModulePosition(),
                        new SwerveModulePosition()
                },
                new Pose2d()
        );

        initializePidgeon.markProcessing();
        WPI_PigeonIMU pigeonIMU = new WPI_PigeonIMU(pidgeonId);
        initializePidgeon.markCompleted();

        OdometryControl odometryControl = new OdometryControl (control, visionControl, pigeonIMU, estimator);;
        addChild.registerLoop(odometryControl, "odometry-loop");


        return odometryControl;
    }
}
