package org.bitbuckets.odometry;

import edu.wpi.first.math.estimator.SwerveDrivePoseEstimator;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.smartdashboard.Field2d;
import org.bitbuckets.drive.controlsds.DriveControl;
import org.bitbuckets.gyro.GyroControl;
import org.bitbuckets.robot.RobotConstants;
import org.bitbuckets.vision.VisionControl;

public class OdometryControl implements Runnable {

    final DriveControl driveControl;

    final VisionControl visionControl;

    final GyroControl gyroControl;

    final SwerveDrivePoseEstimator swerveDrivePoseEstimator;

    public OdometryControl(DriveControl driveControl, VisionControl visionControl, GyroControl gyroControl, Pose2d pose2d) {
        this.driveControl = driveControl;
        this.visionControl = visionControl;
        this.gyroControl = gyroControl;
        this.swerveDrivePoseEstimator = new SwerveDrivePoseEstimator(RobotConstants.KINEMATICS, gyroControl.getGyroAngle(), driveControl.currentPositions(), pose2d);


    }


    public Pose2d estimatedSwervePose() {

        return swerveDrivePoseEstimator.getEstimatedPosition();


    }

    // public void estimatedCombinedPose() {
    //    swerveDrivePoseEstimator.addVisionMeasurement();
    //}


    Field2d field = new Field2d();

    // public void swervePoseStateStdDevs() {
    //     final Vector<N7> stateStdDevs = VecBuilder.fill();
    //}

    //   @Override
    public void teleopPeriodic() {


    }

    @Override
    public void run() {
        Rotation2d gyroangle = gyroControl.getGyroAngle();
        swerveDrivePoseEstimator.update(gyroangle, driveControl.currentPositions());

    }


    /**
     * Should estimate where the robot is in 2d space of the field
     *
     * @return the pose
     */
    // Pose2d estimateRobotPose();
    //
    // Pose3d estimateRobotPose3d();


}
