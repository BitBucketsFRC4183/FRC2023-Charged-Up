package org.bitbuckets.robot;

import org.bitbuckets.drive.DriveSDSSubsystem;

/**
 * This class represents your robot's periodic behavior
 */
public class RobotContainer {

    final DriveSDSSubsystem subsystem;

    public RobotContainer(DriveSDSSubsystem subsystem) {
        this.subsystem = subsystem;
    }

    public void autoPeriodic() {
    }

    //Shouldn't need to do anything here
    public void teleopPeriodic() {
        subsystem.teleopPeriodic();
    }

}
