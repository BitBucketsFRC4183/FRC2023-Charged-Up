package org.bitbuckets.odometry;

import com.ctre.phoenix.sensors.Pigeon2;
import com.ctre.phoenix.sensors.WPI_Pigeon2;
import edu.wpi.first.math.estimator.SwerveDrivePoseEstimator;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import org.bitbuckets.drive.DriveConstants;
import org.bitbuckets.drive.IDriveControl;
import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.log.IDebuggable;
import org.bitbuckets.lib.IProcess;
import org.bitbuckets.vision.IVisionControl;


public class OdometryControlSetup implements ISetup<IOdometryControl> {


    final IDriveControl control;
    final IVisionControl visionControl;

    final int pidgeonId;

    public OdometryControlSetup(IDriveControl control, IVisionControl visionControl, int pidgeonId1) {
        this.control = control;
        this.visionControl = visionControl;
        this.pidgeonId = pidgeonId1;
    }

    @Override
    public IOdometryControl build(IProcess self) {

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

        WPI_Pigeon2 pigeonIMU = new WPI_Pigeon2(pidgeonId);
        pigeonIMU.configFactoryDefault();
        pigeonIMU.configMountPose(Pigeon2.AxisDirection.NegativeX, Pigeon2.AxisDirection.PositiveZ);

        IDebuggable debug = self.getDebuggable();

        OdometryControl odometryControl = new OdometryControl(
                debug,
                control,
                visionControl,
                pigeonIMU,
                estimator);

        self.registerLogLoop(odometryControl::logLoop);
        self.registerLogicLoop(odometryControl::updateOdometryLoop);


        return odometryControl;
    }
}
