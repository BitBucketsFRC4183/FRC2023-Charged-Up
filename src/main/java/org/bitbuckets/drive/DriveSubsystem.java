package org.bitbuckets.drive;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj.Preferences;
import org.bitbuckets.auto.AutoControl;
import org.bitbuckets.auto.AutoPath;
import org.bitbuckets.drive.balance.ClosedLoopsControl;
import org.bitbuckets.drive.controlsds.DriveControl;
import org.bitbuckets.drive.holo.HoloControl;
import org.bitbuckets.lib.log.ILoggable;
import org.bitbuckets.lib.tune.IValueTuner;
import org.bitbuckets.odometry.IOdometryControl;
import org.bitbuckets.robot.RobotStateControl;
import org.bitbuckets.vision.VisionControl;

import java.util.Optional;


/**
 * tags: high priority
 * TODO this is becoming a sort of god class, some of this logic has to break out into smaller subsystems
 */
public class DriveSubsystem {

    final DriveInput input;

    final RobotStateControl robotStateControl;
    final IOdometryControl odometryControl;
    final ClosedLoopsControl closedLoopsControl;
    final DriveControl driveControl;
    final AutoControl autoControl;
    final HoloControl holoControl;
    final VisionControl visionControl;

    final IValueTuner<AutoPath> path;
    final ILoggable<Double> autoTime;


    public enum OrientationChooser {
        FIELD_ORIENTED,
        ROBOT_ORIENTED,
    }

    final IValueTuner<OrientationChooser> orientation;

    public DriveSubsystem(DriveInput input, RobotStateControl robotStateControl, IOdometryControl odometryControl, ClosedLoopsControl closedLoopsControl, DriveControl driveControl, AutoControl autoControl, HoloControl holoControl, VisionControl visionControl, IValueTuner<AutoPath> path, ILoggable<Double> autoTime, IValueTuner<OrientationChooser> orientation) {
        this.input = input;
        this.robotStateControl = robotStateControl;
        this.odometryControl = odometryControl;
        this.closedLoopsControl = closedLoopsControl;
        this.driveControl = driveControl;
        this.autoControl = autoControl;
        this.visionControl = visionControl;
        this.path = path;
        this.autoTime = autoTime;
        this.orientation = orientation;
        this.holoControl = holoControl;
    }

    DriveFSM state = DriveFSM.UNINITIALIZED;

    public void robotPeriodic() {


        switch (state) {
            case UNINITIALIZED:
                if (robotStateControl.isRobotAutonomous()) {
                    state = DriveFSM.AUTO_PATHFINDING;
                    break;
                }
                if (robotStateControl.isRobotTeleop()) {
                    state = DriveFSM.TELEOP_NORMAL;
                    break;
                }

                break;
            case AUTO_PATHFINDING:
                if (robotStateControl.isRobotTeleop()) {
                    state = DriveFSM.TELEOP_NORMAL;
                    break;
                }

                ChassisSpeeds targetChassisSpeeds = autoControl.getAutoChassisSpeeds(
                        path.readValue(),
                        robotStateControl.robotAutonomousTime_seconds(),
                        odometryControl.estimatePose2d()
                );

                autoTime.log(robotStateControl.robotAutonomousTime_seconds());

                driveControl.drive(targetChassisSpeeds);

                break;
            case TELEOP_NORMAL:
                if (robotStateControl.isRobotAutonomous()) {
                    state = DriveFSM.AUTO_PATHFINDING;
                    break;
                }

                if (input.isVisionGoPressed()) {
                    state = DriveFSM.TELEOP_VISION;
                    break;
                }

                if (input.isAutoBalancePressed()) {
                    state = DriveFSM.TELEOP_BALANCING; //do balancing next iteration
                    break;
                }
                if (input.isAutoHeadingPressed()) {
                    state = DriveFSM.TELEOP_AUTOHEADING;
                    break;
                }

                teleopNormal();
                break;
            case TELEOP_BALANCING:
                if (input.isDefaultPressed()) {
                    state = DriveFSM.TELEOP_NORMAL;
                    break;
                }

                teleopBalancing();
                break;
            case TELEOP_VISION:
                if (input.isVisionGoReleased()) {
                    state = DriveFSM.TELEOP_NORMAL;
                    break;
                }

                    teleopVision();
                break;
            case TELEOP_AUTOHEADING:
                if (input.isDefaultPressed()) {
                    state = DriveFSM.TELEOP_NORMAL;
                    break;
                }

                teleopAutoheading();
                break;
        }
    }

    void teleopVision() {
        Optional<VisionControl.PhotonCalculationResult> res = visionControl.visionPoseEstimator();
        if (res.isEmpty()) return;
        Pose2d target = res.get().goalPose.toPose2d();


        ChassisSpeeds speeds = holoControl.calculatePose2D(target, 0);
        driveControl.drive(speeds);
    }

    void teleopNormal() {
        double xOutput = input.getInputX() * driveControl.getMaxVelocity();
        double yOutput = -input.getInputY() * driveControl.getMaxVelocity();
        double rotationOutput = input.getInputRot() * driveControl.getMaxAngularVelocity();

        switch (orientation.readValue()) {
            case FIELD_ORIENTED:
                if (xOutput == 0 && yOutput == 0 && rotationOutput == 0) {
                    driveControl.stopSticky();
                } else {
                    driveControl.drive(
                            ChassisSpeeds.fromFieldRelativeSpeeds(xOutput, yOutput, rotationOutput, odometryControl.estimatePose2d().getRotation())
                    );
                }
                break;
            case ROBOT_ORIENTED:
                if (xOutput == 0 && yOutput == 0 && rotationOutput == 0) {
                    driveControl.stopSticky();
                } else {
                    ChassisSpeeds robotOrient = new ChassisSpeeds(xOutput, yOutput, rotationOutput);
                    driveControl.drive(robotOrient);
                }
                break;
        }
    }

    void teleopBalancing() {

        //This is bad and should be shifted somewhere else
        double BalanceDeadband_deg = Preferences.getDouble(DriveConstants.autoBalanceDeadbandDegKey, DriveConstants.BalanceDeadbandDeg);

        double Roll_deg = odometryControl.getRoll_deg();
        if (Math.abs(Roll_deg) > BalanceDeadband_deg) {
            double output = closedLoopsControl.calculateBalanceOutput(Roll_deg, 0);
            driveControl.drive(new ChassisSpeeds(output, 0.0, 0.0));
        } else {
            driveControl.stopSticky();

        }
    }

    void teleopAutoheading() {

        double IMU_Yaw = Math.toRadians(odometryControl.getYaw_deg());//Math.toRadians(-350);

        //will add logic later
        double setpoint = Math.toRadians(0);
        double error = setpoint - IMU_Yaw;


        double rotationOutputOrient = closedLoopsControl.calculateRotOutputRad(
                IMU_Yaw,
                setpoint
        );
//        if(Math.abs(error) < 180)
//        {
//            rotationOutput = -rotationOutput;
//        }
        if (Math.abs(error) > Math.toRadians(2)) {
            driveControl.drive(
                    new ChassisSpeeds(0, 0, rotationOutputOrient)
            );
        } else {
            driveControl.stop();
        }


    }



}
