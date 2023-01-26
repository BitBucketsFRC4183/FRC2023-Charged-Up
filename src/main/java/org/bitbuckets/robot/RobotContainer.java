package org.bitbuckets.robot;

import org.bitbuckets.arm.ArmSubsystem;
import org.bitbuckets.drive.DriveSDSSubsystem;
import org.bitbuckets.vision.VisionControl;

/**
 * This class represents your robot's periodic behavior
 */
public class RobotContainer {

    final DriveSDSSubsystem driveSubsystem;
    final ArmSubsystem armSubsystem;

    public RobotContainer(DriveSDSSubsystem driveSubsystem, ArmSubsystem armSubsystem) {
        this.driveSubsystem = driveSubsystem;
        this.armSubsystem = armSubsystem;
    final VisionControl visionControl;

    public RobotContainer(DriveSDSSubsystem subsystem, VisionControl visionControl) {
        this.subsystem = subsystem;
        this.visionControl = visionControl;
    }

    public void autoPeriodic() {

    }

    public void robotPeriodic() {
        driveSubsystem.robotPeriodic();
    }

    //Shouldn't need to do anything here
    public void teleopPeriodic() {
        subsystem.teleopPeriodic();
        visionControl.teleopPeriodic();

        armSubsystem.teleopPeriodic();
    }

}
