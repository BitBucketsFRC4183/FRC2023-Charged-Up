package org.bitbuckets.robot;

import org.bitbuckets.arm.ArmSubsystem;
import org.bitbuckets.auto.AutoPath;
import org.bitbuckets.drive.DriveSDSSubsystem;
import org.bitbuckets.lib.tune.IValueTuner;
import org.bitbuckets.drive.DriveSubsystem;

/**
 * This class represents your robot's periodic behavior
 */
public class RobotContainer {

    final DriveSubsystem driveSubsystem;
    final ArmSubsystem armSubsystem;

    public RobotContainer(DriveSubsystem driveSubsystem, ArmSubsystem armSubsystem) {
        this.driveSubsystem = driveSubsystem;
        this.armSubsystem = armSubsystem;
    }

    public void autoPeriodic() {

    }

    public void robotPeriodic() {
        armSubsystem.robotPeriodic();
        driveSubsystem.robotPeriodic();
    }

    //Shouldn't need to do anything here
    public void teleopPeriodic() {
//        armSubsystem.teleopPeriodic();
    }

}
