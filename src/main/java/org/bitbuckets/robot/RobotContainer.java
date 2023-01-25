package org.bitbuckets.robot;

import org.bitbuckets.drive.DriveSDSSubsystem;
import org.bitbuckets.drive.auto.AutoPaths;
import org.bitbuckets.elevator.ElevatorSubsystem;

/**
 * This class represents your robot's periodic behavior
 */
public class RobotContainer {

    final DriveSDSSubsystem subsystem;

    final ElevatorSubsystem elevatorSubsystem;

    public RobotContainer(DriveSDSSubsystem subsystem, ElevatorSubsystem elevatorSubsystem) {
        this.subsystem = subsystem;
        this.elevatorSubsystem = elevatorSubsystem;
    }

    public void autoPeriodic() {
        subsystem.autoPeriodic();
    }

    //Shouldn't need to do anything here
    public void teleopPeriodic() {
        subsystem.teleopPeriodic();
        elevatorSubsystem.teleopPeriodic();

    }

    public void autoInit() {
        subsystem.followAutoPath(AutoPaths.TEST_PATH);
    }

    public void teleopInit() {
        subsystem.driveNormal();
    }
}
