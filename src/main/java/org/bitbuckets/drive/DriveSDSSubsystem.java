package org.bitbuckets.drive;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.bitbuckets.drive.auto.AutoControl;
import org.bitbuckets.drive.auto.AutoPaths;
import org.bitbuckets.drive.controlsds.DriveControlSDS;
import org.bitbuckets.lib.util.MathUtil;


/**
 * subsystemize!
 */
public class DriveSDSSubsystem {

    final DriveInput input;

    double rotOutput = 0.1;
    final DriveControlSDS control;

    final AutoControl autoControl;
    private AutoPaths path;


    public DriveSDSSubsystem(DriveInput input, DriveControlSDS control, AutoControl autoControl) {
        this.input = input;
        this.control = control;
        this.autoControl = autoControl;
    }

    DriveFSM state = DriveFSM.TELEOP_NORMAL;
    Pose2d pose = new Pose2d();
    private Timer m_timer = new Timer();

    public void followAutoPath(AutoPaths path) {
        this.path = path;
        state = DriveFSM.AUTO_PATHFINDING;
    }

    public void driveNormal() {
        state = DriveFSM.TELEOP_NORMAL;
    }

    public void autoPeriodic() {
        switch (state) {
            case AUTO_PATHFINDING:
                //auto stuff for pathfinder etc
                double curTime = m_timer.get();
                var targetChassisSpeeds = autoControl.getAutoChassisSpeeds(path, curTime, pose);
                control.drive(targetChassisSpeeds);

                //PathPlannerTrajectory testPath = PathPlanner.loadPath("test path", new PathConstraints(1,1));


                //for when auto is finished
                if (m_timer.hasElapsed(autoControl.getTrajectoryTime(path))) {
                    state = DriveFSM.TELEOP_NORMAL; //switch to teleop
                }
                break;
        }
    }

    //Needs to stop if we're going fw or bw
    public void teleopPeriodic() {
        SmartDashboard.putNumber("rotoutput", rotOutput);
        SmartDashboard.putNumber("gyroVelX", control.getGyroXYZ_mps()[0]);

        switch (state) {
            case UNINITIALIZED:
                //do nothing
                break;
            case TELEOP_NORMAL:
                if (input.isPidswitches()) {
                    state = DriveFSM.PID_TUNING;
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

                double xOutput = input.getInputX() * control.getMaxVelocity();
                double yOutput = input.getInputY() * control.getMaxVelocity();
                double rotationOutput = input.getInputRot() * control.getMaxAngularVelocity();

                if (xOutput == 0 && yOutput == 0 && rotationOutput == 0) {
                    control.stopSticky();
                } else {
                    ChassisSpeeds desired = new ChassisSpeeds(xOutput, yOutput, rotationOutput);
                    control.drive(desired);
                }

                //check the buttons to make sure we dont want a state transition
                break;
            case TELEOP_BALANCING:
                if (input.isDefaultPressed()) {
                    state = DriveFSM.TELEOP_NORMAL;
                    break;
                }
                double BalanceDeadband_deg = Preferences.getDouble(DriveSDSConstants.autoBalanceDeadbandDegKey, DriveSDSConstants.BalanceDeadbandDeg);

                double Roll_deg = control.getRoll_deg();
                if (Math.abs(Roll_deg) > BalanceDeadband_deg) {
                    double output = control.calculateBalanceOutput(Roll_deg, 0);
                    control.drive(new ChassisSpeeds(output, 0.0, 0.0));
                } else {
                    control.stopSticky();

                }
                break;
            //DO teleop balancing here
            case TELEOP_AUTOHEADING:
                if (input.isDefaultPressed()) {
                    state = DriveFSM.TELEOP_NORMAL;
                    break;
                }
                double IMU_Yaw = Math.toRadians(control.getYaw_deg());//Math.toRadians(-350);

                IMU_Yaw = MathUtil.wrap(IMU_Yaw);

                //will add logic later
                double setpoint = Math.toRadians(90);

                double error = setpoint - IMU_Yaw;

                SmartDashboard.putNumber("AutoOrient_setpoint", Math.toDegrees(setpoint));
                SmartDashboard.putNumber("AutoOrient_wrappedYaw", Math.toDegrees(IMU_Yaw));
                SmartDashboard.putNumber("AutoOrient_Error", Math.toDegrees(error));

                double rotationOutputOrient = control.calculateRotOutputRad(
                        IMU_Yaw,
                        setpoint
                );
//        if(Math.abs(error) < 180)
//        {
//            rotationOutput = -rotationOutput;
//        }
                if (Math.abs(error) > Math.toRadians(2)) {
                    control.drive(
                            new ChassisSpeeds(0, 0, rotationOutputOrient)
                    );
                } else {
                    control.stop();
                }


                break;
            case PID_TUNING:
                if (input.isPidswitches()) {
                    state = DriveFSM.PID_TUNING1;
                    break;
                }
                if (input.isDefaultPressed()) {
                    state = DriveFSM.TELEOP_NORMAL;
                    break;
                }
                rotOutput = 0.1;
                control.drive(
                        new ChassisSpeeds(0, 0, rotOutput));
                break;
            case PID_TUNING1:

                if (input.isPidswitches1()) {
                    state = DriveFSM.PID_TUNING;
                    break;

                }
                if (input.isDefaultPressed()) {
                    state = DriveFSM.TELEOP_NORMAL;
                    break;
                }
                rotOutput = 0.3;
                control.drive(

                        new ChassisSpeeds(0, 0, rotOutput));

                break;

        }
    }
}
