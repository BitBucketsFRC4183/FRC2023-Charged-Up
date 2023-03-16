package org.bitbuckets.odometry;

import edu.wpi.first.math.estimator.SwerveDrivePoseEstimator;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import org.bitbuckets.drive.IDriveControl;
import org.bitbuckets.lib.ILogAs;
import org.bitbuckets.lib.IProcess;
import org.bitbuckets.lib.ISetup;

public class SimOdometryControlSetup implements ISetup<IOdometryControl> {

    final SwerveDriveKinematics kinematics;
    final IDriveControl driveControl;

    public SimOdometryControlSetup(SwerveDriveKinematics kinematics, IDriveControl driveControl) {
        this.kinematics = kinematics;
        this.driveControl = driveControl;
    }

    @Override
    public IOdometryControl build(IProcess self) {



        return new SimOdometryControl(
                kinematics, new SwerveDrivePoseEstimator( //will be reset by auto path anyways
                kinematics,
                Rotation2d.fromDegrees(0),
                new SwerveModulePosition[]{
                        new SwerveModulePosition(),
                        new SwerveModulePosition(),
                        new SwerveModulePosition(),
                        new SwerveModulePosition()
                },
                new Pose2d()
        ),
                driveControl,
                self.generateLogger(ILogAs.POSE, "odoEstimatePose"), self.getDebuggable());
    }
}
