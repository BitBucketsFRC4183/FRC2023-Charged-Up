package org.bitbuckets.robot;

import org.bitbuckets.drive.DriveSDSSubsystem;
import org.bitbuckets.vision.VisionControl;

/**
 * This class represents your robot's periodic behavior
 */
public class RobotContainer {

    final DriveSDSSubsystem subsystem;

    final VisionControl visionControl;

    public RobotContainer(DriveSDSSubsystem subsystem, VisionControl visionControl) {
        this.subsystem = subsystem;
        this.visionControl = visionControl;
    }

    public void autoPeriodic() {
    }

    //Shouldn't need to do anything here
    public void teleopPeriodic() {
        subsystem.teleopPeriodic();
        visionControl.teleopPeriodic();

    }

}
