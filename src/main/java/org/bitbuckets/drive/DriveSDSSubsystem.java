package org.bitbuckets.drive;

import edu.wpi.first.math.kinematics.ChassisSpeeds;
import org.bitbuckets.drive.controlsds.DriveControlSDS;


/**
 * subsystemize!
 */
public class DriveSDSSubsystem {

    final DriveInput input;
    final DriveControlSDS control;

    public DriveSDSSubsystem(DriveInput input, DriveControlSDS control) {
        this.input = input;
        this.control = control;
    }

    DriveFSM state = DriveFSM.TELEOP_NORMAL;

    //Needs to stop if we're going fw or bw
    public void teleopPeriodic() {

        switch (state) {
            case UNINITIALIZED:
                //do nothing
                break;
            case TELEOP_NORMAL:
                if (input.isAutoBalancePressed()) {
                    state = DriveFSM.TELEOP_BALANCING; //do balancing next iteration
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
                //DO teleop balancing here
        }
    }
}
