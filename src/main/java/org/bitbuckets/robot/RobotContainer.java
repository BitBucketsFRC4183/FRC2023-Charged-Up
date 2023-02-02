package org.bitbuckets.robot;

import org.bitbuckets.arm.ArmSubsystem;
import org.bitbuckets.drive.DriveSubsystem;
import org.bitbuckets.elevator.ElevatorSubsystem;
import org.bitbuckets.vision.VisionControl;

/**
 * This class represents your robot's periodic behavior
 */
public class RobotContainer {

    final DriveSubsystem driveSubsystem;
    final ArmSubsystem armSubsystem;
    final VisionControl visionControl;

    public RobotContainer(DriveSubsystem driveSubsystem, ArmSubsystem armSubsystem, VisionControl visionControl) {
    final ElevatorSubsystem elevatorSubsystem;

    public RobotContainer(DriveSubsystem driveSubsystem, ArmSubsystem armSubsystem, VisionControl visionControl, ElevatorSubsystem elevatorSubsystem) {
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
        visionControl.teleopPeriodic();
        armSubsystem.teleopPeriodic();
        //    armSubsystem.teleopPeriodic();

    }

    public void robotPeriodic() {
        armSubsystem.robotPeriodic();
        driveSubsystem.robotPeriodic();
        elevatorSubsystem.robotPeriodic();
    }


}
