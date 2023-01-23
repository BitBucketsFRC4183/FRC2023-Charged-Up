package org.bitbuckets.robot;

import org.bitbuckets.drive.DriveSDSSubsystem;
import org.bitbuckets.drive.auto.AutoPaths;

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

    public void autoInit() {
        subsystem.followAutoPath(AutoPaths.TEST_PATH);
    }

    public void teleopInit() {
        subsystem.driveNormal();
    }
}
