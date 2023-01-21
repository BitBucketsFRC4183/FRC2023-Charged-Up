package org.bitbuckets.robot;

import edu.wpi.first.wpilibj.DriverStation;
import org.bitbuckets.drive.DriveSubsystem;
import org.littletonrobotics.junction.Logger;

/**
 * This class represents your robot's periodic behavior
 */
public class RobotContainer {

    final DriveSubsystem subsystem;

    public RobotContainer(DriveSubsystem subsystem) {
        this.subsystem = subsystem;
    }

    public void autoPeriodic() {



    }

    //Shouldn't need to do anything here
    public void teleopPeriodic() {


        subsystem.teleopPeriodic();
    }

}
