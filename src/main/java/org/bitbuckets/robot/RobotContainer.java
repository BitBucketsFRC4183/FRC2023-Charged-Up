package org.bitbuckets.robot;

import org.bitbuckets.arm.ArmSubsystem;
import org.bitbuckets.drive.DriveSubsystem;
import org.bitbuckets.vision.VisionControl;
import org.bitbuckets.drive.DriveSDSSubsystem;
import org.bitbuckets.elevator.ElevatorSubsystem;

/**
 * This class represents your robot's periodic behavior
 */
public class RobotContainer {

    final DriveSubsystem driveSubsystem;
    final ArmSubsystem armSubsystem;
    final VisionControl visionControl;

    final ElevatorSubsystem elevatorSubsystem;

    public RobotContainer(DriveSDSSubsystem driveSubsystem, ArmSubsystem armSubsystem, ElevatorSubsystem elevatorSubsystem) {
    public RobotContainer(DriveSubsystem driveSubsystem, ArmSubsystem armSubsystem,  VisionControl visionControl) {
        this.driveSubsystem = driveSubsystem;
        this.armSubsystem = armSubsystem;
        this.visionControl = visionControl;

        this.elevatorSubsystem = elevatorSubsystem;

    }

    public void autoPeriodic() {
        }
    //Shouldn't need to do anything here
    public void teleopPeriodic() {
        elevatorSubsystem.teleopPeriodic();
    //    armSubsystem.teleopPeriodic();

    }

    public void robotPeriodic() {
        armSubsystem.robotPeriodic();
        driveSubsystem.robotPeriodic();
        elevatorSubsystem.robotPeriodic();
    }

    //Shouldn't need to do anything here
    public void teleopPeriodic() {
        visionControl.teleopPeriodic();
        armSubsystem.teleopPeriodic();
    }


}
