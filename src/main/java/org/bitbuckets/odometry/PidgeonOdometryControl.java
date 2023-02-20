package org.bitbuckets.odometry;

import com.ctre.phoenix.sensors.WPI_Pigeon2;
import edu.wpi.first.math.VecBuilder;
import edu.wpi.first.math.Vector;
import edu.wpi.first.math.estimator.SwerveDrivePoseEstimator;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.numbers.N3;
import edu.wpi.first.math.numbers.N5;
import edu.wpi.first.math.numbers.N7;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj.Timer;
import org.bitbuckets.drive.IDriveControl;
import org.bitbuckets.lib.log.Debuggable;
import org.bitbuckets.vision.IVisionControl;

import java.util.Optional;

public class PidgeonOdometryControl implements IOdometryControl {

    final Debuggable debuggable;
    final IDriveControl driveControl;
    final IVisionControl visionControl;
    final WPI_Pigeon2 pigeonIMU;
    final SwerveDrivePoseEstimator swerveDrivePoseEstimator;


    public PidgeonOdometryControl(Debuggable debuggable, IDriveControl driveControl, IVisionControl visionControl, WPI_Pigeon2 pigeonIMU, SwerveDrivePoseEstimator swerveDrivePoseEstimator) {
        this.debuggable = debuggable;
        this.driveControl = driveControl;
        this.pigeonIMU = pigeonIMU;
        this.swerveDrivePoseEstimator = swerveDrivePoseEstimator;
        this.visionControl = visionControl;
    }

    private static final Vector<N7> stateStdDevs = VecBuilder.fill(0.05, 0.05, Units.degreesToRadians(5), 0.05, 0.05, 0.05, 0.05);

    private static final Vector<N5> localMeasurementStdDevs = VecBuilder.fill(Units.degreesToRadians(0.01), 0.01, 0.01, 0.01, 0.01);
    private static final Vector<N3> visionMeasurementStdDevs = VecBuilder.fill(0.5, 0.5, Units.degreesToRadians(10));


    public void updateOdometryLoop() {
        Rotation2d gyroangle = (pigeonIMU.getRotation2d());
        double epoch = Timer.getFPGATimestamp();
        debuggable.log("raw-swerve-pose", swerveDrivePoseEstimator.update(gyroangle, driveControl.currentPositions()));

        //Todo: re add when vision is fixed
        Optional<Pose3d> res = visionControl.estimateVisionRobotPose();
        if (res == null) return;

        if (res != null && res.isPresent()) {
            Pose2d realPose = res.get().toPose2d();
            swerveDrivePoseEstimator.addVisionMeasurement(realPose, epoch, visionMeasurementStdDevs);


        } else {


        }

    }

    public void logLoop() {
        debuggable.log("yaw", pigeonIMU.getYaw());
        debuggable.log("pitch", pigeonIMU.getPitch());
        debuggable.log("roll", pigeonIMU.getRoll());
        debuggable.log("rate", pigeonIMU.getRate());
        debuggable.log("fused-pose", estimateFusedPose2d());
    }

    @Override
    public Pose2d estimateFusedPose2d() {
        return swerveDrivePoseEstimator.getEstimatedPosition();
    }


    @Override
    public Rotation2d getRotation2d() {
        return pigeonIMU.getRotation2d();
    }

    @Override
    public double getYaw_deg() {
        return pigeonIMU.getYaw();
    }


    @Override
    public double getRoll_deg() {
        return pigeonIMU.getRoll();
    }

    @Override
    public void zero() {
        pigeonIMU.reset();
    }

    @Override
    public void setPos(Rotation2d gyroAngle, SwerveModulePosition[] modulePositions, Pose2d poseMeters) {
        swerveDrivePoseEstimator.resetPosition(gyroAngle, modulePositions, poseMeters);
    }


}
