package org.bitbuckets.drive;

import com.pathplanner.lib.PathConstraints;
import com.pathplanner.lib.PathPlanner;
import com.pathplanner.lib.PathPlannerTrajectory;
import edu.wpi.first.math.controller.HolonomicDriveController;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.bitbuckets.drive.control.DriveControl;
import org.bitbuckets.drive.module.AutoControl;
import org.bitbuckets.robot.RobotConstants;

import java.util.function.Consumer;
import java.util.function.Supplier;


/**
 * subsystemize!
 */
public class DriveSubsystem {

    final DriveInput input;
    final DriveControl control;

    private final Timer m_timer = new Timer();
    private final PathPlannerTrajectory m_trajectory;
    private final Supplier<Pose2d> m_pose;
    private final SwerveDriveKinematics m_kinematics;
    private final HolonomicDriveController m_controller;
    private final Consumer<SwerveModuleState[]> m_outputModuleStates;


    DriveFSM state = DriveFSM.TELEOP_NORMAL;

    public DriveSubsystem(DriveInput input, DriveControl control, PathPlannerTrajectory m_trajectory, Supplier<Pose2d> m_pose, SwerveDriveKinematics m_kinematics, HolonomicDriveController m_controller, Consumer<SwerveModuleState[]> m_outputModuleStates) {
        this.input = input;
        this.control = control;

        this.m_trajectory = m_trajectory;
        this.m_pose = m_pose;
        this.m_kinematics = m_kinematics;
        this.m_controller = m_controller;
        this.m_outputModuleStates = m_outputModuleStates;
    }

    //Needs to stop if we're going fw or bw
    public void teleopPeriodic() {

        switch (state) {
            case UNINITIALIZED:
                //if the robot is unititalized, shouldn't it switch to auto?
                //I'll put this here for now but i'll move it if necessary
                state = DriveFSM.AUTO_NORMAL;

            case TELEOP_NORMAL:
                ChassisSpeeds desired = new ChassisSpeeds(input.getInputX(), input.getInputY() , input.getInputRot() * DriveConstants.MAX_ANG_VELOCITY);
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




                //for when auto is finished
                if (input.isAutoPathFinished()) {
                    state = DriveFSM.TELEOP_NORMAL; //switch to teleop
                }

        }




    }

    void driveAt(ChassisSpeeds speeds) {
        SwerveModuleState[] states = RobotConstants.KINEMATICS.toSwerveModuleStates(speeds);
        SwerveDriveKinematics.desaturateWheelSpeeds(states, DriveConstants.MAX_DRIVE_VELOCITY);
        control.doDriveWithStates(states);
    }


    public void driveForward()
    {
        driveAt(new ChassisSpeeds(0.05, 0.0, 0.0));
    }

    public void driveBack() {

        driveAt(new ChassisSpeeds(-0.05, 0.0, 0.0));
    }

}
