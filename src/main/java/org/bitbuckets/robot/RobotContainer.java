package org.bitbuckets.robot;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.bitbuckets.arm.ArmSubsystem;
import org.bitbuckets.drive.DriveSubsystem;
import org.bitbuckets.elevator.ElevatorSubsystem;
import org.bitbuckets.vision.IVisionControl;
import org.bitbuckets.vision.VisionControl;

/**
 * This class represents your robot's periodic behavior
 */
public class RobotContainer {

    final DriveSubsystem driveSubsystem;
    final ArmSubsystem armSubsystem;
    final ElevatorSubsystem elevatorSubsystem;

    final IVisionControl visionControl;

    public RobotContainer(DriveSubsystem driveSubsystem, ArmSubsystem armSubsystem, ElevatorSubsystem elevatorSubsystem, IVisionControl visionControl) {
        this.driveSubsystem = driveSubsystem;
        this.armSubsystem = armSubsystem;
        this.elevatorSubsystem = elevatorSubsystem;
        this.visionControl = visionControl;
    }

    public void autoPeriodic() {

    }

    public void robotPeriodic() {
        driveSubsystem.robotPeriodic();
    }


    int i = 0;

    //Shouldn't need to do anything here
    public void teleopPeriodic() {
        SmartDashboard.putNumber("debugIncrement", ++i);
        elevatorSubsystem.teleopPeriodic();
        armSubsystem.teleopPeriodic();


    }


}
