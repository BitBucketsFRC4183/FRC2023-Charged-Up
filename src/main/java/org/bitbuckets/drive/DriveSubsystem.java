package org.bitbuckets.drive;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.wpilibj.Timer;
import org.bitbuckets.drive.control.DriveControl;
import org.bitbuckets.drive.module.AutoControl;
import org.bitbuckets.robot.RobotConstants;


/**
 * subsystemize!
 */
public class DriveSubsystem {

    final DriveInput input;
    final DriveControl control;

    final AutoControl autoControl;

    private Timer m_timer = new Timer();

    DriveFSM state = DriveFSM.TELEOP_NORMAL;
    Pose2d pose = new Pose2d();

    public DriveSubsystem(DriveInput input, DriveControl control, AutoControl autoControl) {
        this.input = input;
        this.control = control;
        this.autoControl = autoControl;
    }

    //Needs to stop if we're going fw or bw
    public void teleopPeriodic() {

        switch (state) {
            case UNINITIALIZED:
                //if the robot is unititalized, shouldn't it switch to auto?
                //I'll put this here for now but i'll move it if necessary
                state = DriveFSM.AUTO_NORMAL;

            case TELEOP_NORMAL:
                ChassisSpeeds desired = new ChassisSpeeds(input.getInputX(), input.getInputY(), input.getInputRot() * DriveConstants.MAX_ANG_VELOCITY);
                driveAt(desired);

                if (input.isAutoBalancePressed()) {
                    state = DriveFSM.TELEOP_BALANCING; //do balancing next iteration
                }

                //check the buttons to make sure we dont want a state transition

            case TELEOP_BALANCING:
                //DO teleop balancing here

            case AUTO_PATHFINDING:
                //auto stuff for pathfinder etc
                double curTime = m_timer.get();
                var targetChassisSpeeds = autoControl.getAutoChassisSpeeds(curTime, pose);
                driveAt(targetChassisSpeeds);

                //PathPlannerTrajectory testPath = PathPlanner.loadPath("test path", new PathConstraints(1,1));


                //for when auto is finished
                if (m_timer.hasElapsed(autoControl.getTrajectoryTime())) {
                    state = DriveFSM.TELEOP_NORMAL; //switch to teleop
                }

        }


    }

    void driveAt(ChassisSpeeds speeds) {
        SwerveModuleState[] states = RobotConstants.KINEMATICS.toSwerveModuleStates(speeds);
        SwerveDriveKinematics.desaturateWheelSpeeds(states, DriveConstants.MAX_DRIVE_VELOCITY);
        control.doDriveWithStates(states);
    }


    public void driveForward() {
        driveAt(new ChassisSpeeds(0.05, 0.0, 0.0));
    }

    public void driveBack() {

        driveAt(new ChassisSpeeds(-0.05, 0.0, 0.0));
    }

}
