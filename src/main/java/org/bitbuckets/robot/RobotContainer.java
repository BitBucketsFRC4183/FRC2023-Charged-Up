package org.bitbuckets.robot;

import org.bitbuckets.arm.ArmSubsystem;
import org.bitbuckets.drive.DriveSDSSubsystem;
import org.bitbuckets.drive.controlsds.DriveControlSDS;
import org.bitbuckets.drive.module.ChaseTagCommand;
import org.bitbuckets.vision.VisionControl;

/**
 * This class represents your robot's periodic behavior
 */
public class RobotContainer {

    final DriveSubsystem driveSubsystem;
    final ArmSubsystem armSubsystem;
    final VisionControl visionControl;

    final DriveControlSDS driveControlSDS;




    public RobotContainer(DriveSDSSubsystem driveSubsystem, ArmSubsystem armSubsystem, VisionControl visionControl, DriveControlSDS driveControlSDS) {
        this.driveSubsystem = driveSubsystem;
        this.armSubsystem = armSubsystem;
        this.visionControl = visionControl;

        this.driveControlSDS = driveControlSDS;
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
