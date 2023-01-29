package org.bitbuckets.odometry;

import edu.wpi.first.math.estimator.SwerveDrivePoseEstimator;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.SwerveDriveOdometry;
import edu.wpi.first.wpilibj.smartdashboard.Field2d;
import org.bitbuckets.drive.IDriveControl;

import org.bitbuckets.drive.controlsds.DriveControlSDS;
import org.bitbuckets.robot.RobotConstants;

import static org.bitbuckets.drive.controlsds.DriveControlSDS.gyro;

public class OdometryControl {

    final IDriveControl driveControl;

    final DriveControlSDS driveControlSDS;

    final SwerveDriveOdometry swerveDriveOdometry;

    final SwerveDrivePoseEstimator swerveDrivePoseEstimator;

    public OdometryControl(IDriveControl driveControl, DriveControlSDS driveControlSDS, Pose2d pose2d) {
        this.driveControl = driveControl;
        this.driveControlSDS = driveControlSDS;
        this.swerveDriveOdometry = new SwerveDriveOdometry(RobotConstants.KINEMATICS, gyroangle, driveControl.currentPositions(), pose2d);
        this.swerveDrivePoseEstimator = new SwerveDrivePoseEstimator(RobotConstants.KINEMATICS, gyroangle, driveControl.currentPositions(), pose2d);


    }




    Rotation2d gyroangle = Rotation2d.fromRadians(gyro.getCompassHeading());





    public void estimatedSwervePose() {
        swerveDrivePoseEstimator.update(gyroangle, driveControl.currentPositions());
        swerveDrivePoseEstimator.getEstimatedPosition();


    }

   public void estimatedCombinedPose() {
       swerveDrivePoseEstimator.addVisionMeasurement();
   }



    static Field2d field = new Field2d();

 //   @Override
    public void teleopPeriodic() {


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
