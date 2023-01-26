package org.bitbuckets.robot;

import org.bitbuckets.arm.ArmSubsystem;
import org.bitbuckets.drive.DriveSDSSubsystem;
import org.bitbuckets.drive.auto.AutoPaths;
import org.bitbuckets.elevator.ElevatorSubsystem;

/**
 * This class represents your robot's periodic behavior
 */
public class RobotContainer {

    final DriveSDSSubsystem driveSubsystem;
    final ArmSubsystem armSubsystem;

    public RobotContainer(DriveSDSSubsystem driveSubsystem, ArmSubsystem armSubsystem) {
        this.driveSubsystem = driveSubsystem;
        this.armSubsystem = armSubsystem;
    final ElevatorSubsystem elevatorSubsystem;

    public RobotContainer(DriveSDSSubsystem subsystem, ElevatorSubsystem elevatorSubsystem) {
        this.subsystem = subsystem;
        this.elevatorSubsystem = elevatorSubsystem;
    }

    public void autoPeriodic() {

    //Shouldn't need to do anything here
    public void teleopPeriodic() {
        subsystem.teleopPeriodic();
        elevatorSubsystem.teleopPeriodic();

    }

    public void robotPeriodic() {
        driveSubsystem.robotPeriodic();
    }

    //Shouldn't need to do anything here
    public void teleopPeriodic() {
        armSubsystem.teleopPeriodic();
    }

}
