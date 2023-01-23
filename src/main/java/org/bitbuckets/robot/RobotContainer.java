package org.bitbuckets.robot;

import edu.wpi.first.wpilibj.DriverStation;
import org.bitbuckets.drive.DriveSubsystem;
import org.littletonrobotics.junction.Logger;

/**
 * This class represents your robot's periodic behavior
 */
public class RobotContainer {

    //  final DriveSubsystem driveSubsystem;
    final ArmSubsystem armSubsystem;

    public RobotContainer(ArmSubsystem armSubsystem) {
        //this.driveSubsystem = driveSubsystem;
        this.armSubsystem = armSubsystem;
    }

    public void autoPeriodic() {


    }

    //Shouldn't need to do anything here
    public void teleopPeriodic() {

        armSubsystem.teleopPeriodic();
        // driveSubsystem.teleopPeriodic();
    }

    public void autoInit() {
        subsystem.followAutoPath(AutoPaths.TEST_PATH);
    }

    public void teleopInit() {
        subsystem.driveNormal();
    }
}
