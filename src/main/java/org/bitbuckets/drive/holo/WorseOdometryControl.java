package org.bitbuckets.drive.holo;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import org.bitbuckets.drive.IDriveControl;
import org.bitbuckets.lib.hardware.IGyro;
import org.bitbuckets.odometry.IOdometryControl;

public class WorseOdometryControl implements IOdometryControl {

    final SwerveDriveKinematics kinematics;
    final IDriveControl driveControl;
    final IGyro gyro;

    OdometryInstance odometryInstance;

    public WorseOdometryControl(SwerveDriveKinematics kinematics, IDriveControl driveControl, IGyro gyro, OdometryInstance instance) {
        this.kinematics = kinematics;
        this.driveControl = driveControl;
        this.gyro = gyro;
        this.odometryInstance = instance;
    }

    @Override
    public Pose2d estimatePose_trueFieldPose() {

        //TODO we assume that true field pose IS initialization relative BECAUSE initialization relative is initialized by the super epic cool machine.
        return odometryInstance.estimateInitializationRelativePose();
    }

    @Override
    public IGyro getGyro() {
        return gyro;
    }


    @Override
    public void setPos(Pose2d pose_trueFieldRelative) {

        //Set it to a new odometry instance. I hate this.
        odometryInstance = new OdometryInstance(kinematics, driveControl, pose_trueFieldRelative, driveControl.currentPositions_initializationRelative());
    }

    @Override
    public void zeroOdo() {
        throw new UnsupportedOperationException();
    }
}
