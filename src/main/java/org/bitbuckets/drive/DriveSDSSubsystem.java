package org.bitbuckets.drive;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.bitbuckets.auto.AutoControl;
import org.bitbuckets.auto.AutoPath;
import org.bitbuckets.drive.balance.AutoAxisControl;
import org.bitbuckets.drive.controlsds.DriveControlSDS;
import org.bitbuckets.gyro.GyroControl;
import org.bitbuckets.lib.tune.IValueTuner;
import org.bitbuckets.robot.RobotStateControl;


/**
 * tags: high priority
 * TODO this is becoming a sort of god class, some of this logic has to break out into smaller subsystems
 */
public class DriveSDSSubsystem {

    final DriveInput input;

    final RobotStateControl robotStateControl;
    final GyroControl gyroControl;
    final AutoAxisControl autoAxisControl;
    final DriveControlSDS driveControl;
    final AutoControl autoControl;

    final IValueTuner<AutoPath> path;


    public DriveSDSSubsystem(DriveInput input, RobotStateControl robotStateControl, GyroControl gyroControl, AutoAxisControl autoAxisControl, DriveControlSDS driveControl, AutoControl autoControl, IValueTuner<AutoPath> path) {
        this.input = input;
        this.robotStateControl = robotStateControl;
        this.gyroControl = gyroControl;
        this.autoAxisControl = autoAxisControl;
        this.driveControl = driveControl;
        this.autoControl = autoControl;
        this.path = path;
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

                //TODO AutoControl should read from drive odometry
                ChassisSpeeds targetChassisSpeeds = autoControl.getAutoChassisSpeeds(
                        path.readValue(),
                        robotStateControl.robotAutonomousTime_seconds(),
                        new Pose2d()
                );

                driveControl.drive(targetChassisSpeeds);

                break;
            case TELEOP_NORMAL:
            
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
            case TELEOP_AUTOHEADING:
                if (input.isDefaultPressed()) {
                    state = DriveFSM.TELEOP_NORMAL;
                    break;
                }

                teleopAutoheading();
                break;
        }
    }

    void teleopNormal() {
        double xOutput = input.getInputX() * driveControl.getMaxVelocity();
        double yOutput = input.getInputY() * driveControl.getMaxVelocity();
        double rotationOutput = input.getInputRot() * driveControl.getMaxAngularVelocity();

        if (xOutput == 0 && yOutput == 0 && rotationOutput == 0) {
            driveControl.stopSticky();
        } else {
            ChassisSpeeds desired = new ChassisSpeeds(xOutput, yOutput, rotationOutput);
            driveControl.drive(desired);
        }
    }

    void teleopBalancing() {

        //This is bad and should be shifted somewhere else
        double BalanceDeadband_deg = Preferences.getDouble(DriveSDSConstants.autoBalanceDeadbandDegKey, DriveSDSConstants.BalanceDeadbandDeg);

        double Roll_deg = gyroControl.getRoll_deg();
        if (Math.abs(Roll_deg) > BalanceDeadband_deg) {
            double output = autoAxisControl.calculateBalanceOutput(Roll_deg, 0);
            driveControl.drive(new ChassisSpeeds(output, 0.0, 0.0));
        } else {
            driveControl.stopSticky();

        }
    }

    void teleopAutoheading() {

        double IMU_Yaw = Math.toRadians(gyroControl.getYaw_deg());//Math.toRadians(-350);

        //will add logic later
        double setpoint = Math.toRadians(0);
        double error = setpoint - IMU_Yaw;

        SmartDashboard.putNumber("AutoOrient_setpoint", Math.toDegrees(setpoint));
        SmartDashboard.putNumber("AutoOrient_wrappedYaw", Math.toDegrees(IMU_Yaw));
        SmartDashboard.putNumber("AutoOrient_Error", Math.toDegrees(error));

        double rotationOutputOrient = autoAxisControl.calculateRotOutputRad(
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
