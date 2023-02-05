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


public class OdometryControlSetup implements ISetup<OdometryControl> {


    final IDriveControl control;
    WPI_PigeonIMU pigeonIMU;


    public OdometryControlSetup(IDriveControl control, WPI_PigeonIMU pigeonIMU) {
        this.control = control;
        this.pigeonIMU = pigeonIMU;
    }

    @Override
    public OdometryControl build(ProcessPath addChild) {
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

        OdometryControl odometryControl = new OdometryControl (control, estimator, pigeonIMU);;

        addChild.registerLoop(odometryControl, "odometry-loop");

        return odometryControl;
    }
}
