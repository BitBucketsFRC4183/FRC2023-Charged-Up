package org.bitbuckets.robot;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.bitbuckets.RGB.RgbSubsystem;
import org.bitbuckets.arm.ArmSubsystem;
import org.bitbuckets.auto.AutoSubsystem;
import org.bitbuckets.drive.DriveSubsystem;
import org.bitbuckets.elevator.ElevatorSubsystem;
import org.bitbuckets.macros.MacroSubsystem;

/**
 * This class represents your robot's periodic behavior
 */
public class RobotContainer {


    final DriveSubsystem driveSubsystem;
    final ArmSubsystem armSubsystem;
    final ElevatorSubsystem elevatorSubsystem;
    final AutoSubsystem autoSubsystem;
    final MacroSubsystem macroSubsystem;
    final RgbSubsystem rgbSubsystem;

    public RobotContainer(DriveSubsystem driveSubsystem, ArmSubsystem armSubsystem, ElevatorSubsystem elevatorSubsystem, AutoSubsystem autoSubsystem, MacroSubsystem macroSubsystem, RgbSubsystem rgbSubsystem) {
        this.driveSubsystem = driveSubsystem;
        this.armSubsystem = armSubsystem;
        this.elevatorSubsystem = elevatorSubsystem;
        this.autoSubsystem = autoSubsystem;
        this.macroSubsystem = macroSubsystem;
        this.rgbSubsystem = rgbSubsystem;
    }


    public void robotPeriodic() {
        autoSubsystem.runLoop();
        driveSubsystem.runLoop();
        elevatorSubsystem.robotPeriodic();
        rgbSubsystem.robotPeriodic();
        armSubsystem.runLoop();
    }


    int i = 0;

    //Shouldn't need to do anything here
    public void teleopPeriodic() {
        SmartDashboard.putNumber("debugIncrement", ++i);
        elevatorSubsystem.teleopPeriodic();
        armSubsystem.teleopPeriodic();
        macroSubsystem.teleopPeriodic();

    }


}
