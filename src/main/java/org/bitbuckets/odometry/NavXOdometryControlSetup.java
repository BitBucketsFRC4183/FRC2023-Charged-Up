package org.bitbuckets.odometry;

import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.math.estimator.SwerveDrivePoseEstimator;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.wpilibj.SPI;
import org.bitbuckets.drive.DriveConstants;
import org.bitbuckets.drive.IDriveControl;
import org.bitbuckets.lib.IProcess;
import org.bitbuckets.lib.ISetup;
import org.bitbuckets.vision.IVisionControl;


public class NavXOdometryControlSetup implements ISetup<IOdometryControl> {


    final IDriveControl control;
    final IVisionControl visionControl;

    final int AHRSId;

    public NavXOdometryControlSetup(IDriveControl control, IVisionControl visionControl, int AHRSId) {
        this.control = control;
        this.visionControl = visionControl;
        this.AHRSId = AHRSId;

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

        AHRS navXGyro = new AHRS(SPI.Port.kMXP);


        return new NavXOdometryControl(
                self.getDebuggable(),
                control,
                estimator,
                navXGyro,
                visionControl
        );
    }
}

