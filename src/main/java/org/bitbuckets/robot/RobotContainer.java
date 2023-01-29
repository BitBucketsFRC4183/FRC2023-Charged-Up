package org.bitbuckets.robot;

import org.bitbuckets.arm.ArmSubsystem;
import org.bitbuckets.drive.DriveSDSSubsystem;

/**
 * This class represents your robot's periodic behavior
 */
public class RobotContainer {

    final DriveSDSSubsystem driveSubsystem;
    //final ArmSubsystem armSubsystem;

    public RobotContainer(DriveSDSSubsystem driveSubsystem) {
        this.driveSubsystem = driveSubsystem;
        //this.armSubsystem = armSubsystem;
    }

    public void autoPeriodic() {

    }

    public void robotPeriodic() {
        driveSubsystem.robotPeriodic();
    }

    //Shouldn't need to do anything here
    public void teleopPeriodic() {
//        armSubsystem.teleopPeriodic();
    }

}
