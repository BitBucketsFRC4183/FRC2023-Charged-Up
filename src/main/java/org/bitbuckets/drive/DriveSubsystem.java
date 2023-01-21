package org.bitbuckets.drive;

import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import org.bitbuckets.drive.control.DriveControl;
import org.bitbuckets.robot.RobotConstants;



/**
 * subsystemize!
 */
public class DriveSubsystem {

    final DriveInput input;
    final DriveControl control;

    public DriveSubsystem(DriveInput input, DriveControl control) {
        this.input = input;
        this.control = control;
    }

    DriveFSM state = DriveFSM.TELEOP_NORMAL;

    //Needs to stop if we're going fw or bw
    public void teleopPeriodic() {

        switch (state) {
            case UNINITIALIZED:
                //do nothing

            case TELEOP_NORMAL:
                ChassisSpeeds desired = new ChassisSpeeds(input.getInputX(), input.getInputY() , input.getInputRot() * DriveConstants.MAX_ANG_VELOCITY);
                driveAt(desired);

                if (input.isAutoBalancePressed()) {
                    state = DriveFSM.TELEOP_BALANCING; //do balancing next iteration
                }

                //check the buttons to make sure we dont want a state transition

            case TELEOP_BALANCING:
                //DO teleop balancing here

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
