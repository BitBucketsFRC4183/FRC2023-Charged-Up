package org.bitbuckets.drive.controlsds;

import edu.wpi.first.math.kinematics.SwerveModuleState;
import org.bitbuckets.lib.log.Loggable;

@Loggable
public class DriveControlSDSData {

    SwerveModuleState[] targetStates;
    SwerveModuleState[] realStates;

}
