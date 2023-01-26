package org.bitbuckets.robot;

import org.bitbuckets.arm.ArmSubsystem;
import org.bitbuckets.drive.DriveSDSSubsystem;
import org.bitbuckets.elevator.ElevatorSubsystem;

/**
 * This class represents your robot's periodic behavior
 */
public class RobotContainer {

    final DriveSDSSubsystem driveSubsystem;
    final ArmSubsystem armSubsystem;

    final ElevatorSubsystem elevatorSubsystem;

    public RobotContainer(DriveSDSSubsystem driveSubsystem, ArmSubsystem armSubsystem, ElevatorSubsystem elevatorSubsystem) {
        this.driveSubsystem = driveSubsystem;
        this.armSubsystem = armSubsystem;
        this.elevatorSubsystem = elevatorSubsystem;

    }

    public void autoPeriodic() {
        }
    //Shouldn't need to do anything here
    public void teleopPeriodic() {
        elevatorSubsystem.teleopPeriodic();
        armSubsystem.teleopPeriodic();

    }

    public void robotPeriodic() {
        driveSubsystem.robotPeriodic();
    }



}
