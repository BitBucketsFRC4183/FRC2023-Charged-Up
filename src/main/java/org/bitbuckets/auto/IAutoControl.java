package org.bitbuckets.auto;

import edu.wpi.first.math.kinematics.SwerveModulePosition;
import org.bitbuckets.odometry.IOdometryControl;

public interface IAutoControl {

    AutoPathInstance generateAndStartPath(AutoPath whichOne, SwerveModulePosition[] swerveModulePositions, IOdometryControl odometryControl);


}
