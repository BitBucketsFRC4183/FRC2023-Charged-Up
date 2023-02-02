package org.bitbuckets.robot;

import org.bitbuckets.arm.ArmSubsystem;
import org.bitbuckets.auto.AutoControl;
import org.bitbuckets.drive.DriveSubsystem;
import org.bitbuckets.vision.VisionControl;

/**
 * This class represents your robot's periodic behavior
 */
public class RobotContainer {

    final DriveSubsystem driveSubsystem;
    final ArmSubsystem armSubsystem;
    final VisionControl visionControl;
    final AutoControl autoControl;

    public RobotContainer(DriveSubsystem driveSubsystem, ArmSubsystem armSubsystem, VisionControl visionControl, AutoControl autoControl) {
        this.driveSubsystem = driveSubsystem;
        this.armSubsystem = armSubsystem;
        this.visionControl = visionControl;
        this.autoControl = autoControl;
    }

    public void autoPeriodic() {
        
    }

    public void robotPeriodic() {
        armSubsystem.robotPeriodic();
        driveSubsystem.robotPeriodic();
    }

    //Shouldn't need to do anything here
    public void teleopPeriodic() {
        visionControl.teleopPeriodic();
        armSubsystem.teleopPeriodic();
    }

}
