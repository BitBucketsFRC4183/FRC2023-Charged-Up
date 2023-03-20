package org.bitbuckets.odometry;

import edu.wpi.first.math.Vector;
import edu.wpi.first.math.estimator.SwerveDrivePoseEstimator;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.numbers.N3;
import org.bitbuckets.drive.IDriveControl;
import org.bitbuckets.lib.ILogAs;
import org.bitbuckets.lib.IProcess;
import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.hardware.IGyro;
import org.bitbuckets.vision.IVisionControl;

public class OdometryControlSetup implements ISetup<IOdometryControl> {

    final Vector<N3> stdDevs;
    final SwerveDriveKinematics kinematics;
    final IDriveControl driveControl;
    final IVisionControl visionControl;

    final ISetup<IGyro> gyroSetup;

    public OdometryControlSetup(Vector<N3> stdDevs, SwerveDriveKinematics kinematics, IDriveControl driveControl, IVisionControl visionControl, ISetup<IGyro> gyroSetup) {
        this.stdDevs = stdDevs;
        this.kinematics = kinematics;
        this.driveControl = driveControl;
        this.visionControl = visionControl;
        this.gyroSetup = gyroSetup;
    }

    @Override
    public IOdometryControl build(IProcess self) {

        IGyro gyro = self.childSetup("gyro", gyroSetup);


        return new OdometryControl(
                stdDevs,
                new SwerveDrivePoseEstimator( //will be reset by auto path anyways
                        kinematics,
                        gyro.getRotation2d(),
                        driveControl.currentPositions(),
                        new Pose2d()
                ),
                driveControl,
                visionControl,
                gyro,
                self.generateLogger(ILogAs.POSE, "odoEstimatePose"),
                self.generateLogger(ILogAs.POSE, "visionEstimatePose"),
                self.getDebuggable());
    }
}
