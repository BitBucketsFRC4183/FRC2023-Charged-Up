package org.bitbuckets.robot;

import org.bitbuckets.arm.ArmSubsystem;

/**
 * This class represents your robot's periodic behavior
 */
public class RobotContainer {

    //  final DriveSubsystem driveSubsystem;
    final ArmSubsystem armSubsystem;

    public RobotContainer(ArmSubsystem armSubsystem) {
        //this.driveSubsystem = driveSubsystem;
        this.armSubsystem = armSubsystem;
    }

    public void autoPeriodic() {


    }

    //Shouldn't need to do anything here
    public void teleopPeriodic() {

        armSubsystem.teleopPeriodic();
        // driveSubsystem.teleopPeriodic();
    }

}
