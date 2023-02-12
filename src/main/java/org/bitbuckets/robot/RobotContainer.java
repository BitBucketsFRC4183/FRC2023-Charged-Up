package org.bitbuckets.robot;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.bitbuckets.arm.ArmSubsystem;
import org.bitbuckets.auto.AutoSubsystem;
import org.bitbuckets.drive.DriveSubsystem;
import org.bitbuckets.elevator.ElevatorSubsystem;

/**
 * This class represents your robot's periodic behavior
 */
public class RobotContainer {


    final DriveSubsystem driveSubsystem;
    final ArmSubsystem armSubsystem;
    final ElevatorSubsystem elevatorSubsystem;
    final AutoSubsystem autoSubsystem;

    public RobotContainer(DriveSubsystem driveSubsystem, ArmSubsystem armSubsystem, ElevatorSubsystem elevatorSubsystem, AutoSubsystem autoSubsystem) {
        this.driveSubsystem = driveSubsystem;
        this.armSubsystem = armSubsystem;
        this.elevatorSubsystem = elevatorSubsystem;
        this.autoSubsystem = autoSubsystem;
    }


    public void robotPeriodic() {
        autoSubsystem.runLoop();
        driveSubsystem.runLoop();
        elevatorSubsystem.robotPeriodic();
    }


    //Shouldn't need to do anything here
    public void teleopPeriodic() {
        elevatorSubsystem.teleopPeriodic();
        armSubsystem.teleopPeriodic();
    }


}
