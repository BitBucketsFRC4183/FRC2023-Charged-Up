package org.bitbuckets.robot;

import org.bitbuckets.arm.ArmSubsystem;
import org.bitbuckets.auto.AutoControl;

import org.bitbuckets.drive.DriveSubsystem;
import org.bitbuckets.elevator.ElevatorSubsystem;
import org.bitbuckets.drive.module.ChaseTagCommand;
import org.bitbuckets.vision.VisionControl;

/**
 * This class represents your robot's periodic behavior
 */
public class RobotContainer {

    final DriveSubsystem driveSubsystem;
    final ArmSubsystem armSubsystem;
    final VisionControl visionControl;
    final AutoControl autoControl;
    final ElevatorSubsystem elevatorSubsystem;
    final ChaseTagCommand chaseTagCommand;



    public RobotContainer(DriveSubsystem driveSubsystem, ArmSubsystem armSubsystem, VisionControl visionControl, ElevatorSubsystem elevatorSubsystem, AutoControl autoControl) {


    public RobotContainer(DriveSubsystem driveSubsystem, ArmSubsystem armSubsystem, VisionControl visionControl, ChaseTagCommand chaseTagCommand) {
        this.driveSubsystem = driveSubsystem;
        this.armSubsystem = armSubsystem;
        this.visionControl = visionControl;
        this.autoControl = autoControl;
        this.elevatorSubsystem = elevatorSubsystem;

        this.chaseTagCommand = chaseTagCommand;
    }

    public void autoPeriodic() {

    }

        // TODO make drive subsystem happen bc makevisionmode ignores that
    public void robotPeriodic() {
        driveSubsystem.makeVisionMode();
        armSubsystem.robotPeriodic();

        driveSubsystem.robotPeriodic();
    }

    //Shouldn't need to do anything here
    public void teleopPeriodic() {
        elevatorSubsystem.teleopPeriodic();
        visionControl.teleopPeriodic();
        armSubsystem.teleopPeriodic();
        //    armSubsystem.teleopPeriodic();

    }




}
