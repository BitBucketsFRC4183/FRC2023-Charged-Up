package org.bitbuckets.drive;

import edu.wpi.first.math.kinematics.SwerveModulePosition;

public interface IDriveControl {

    SwerveModulePosition[] currentPositions();

}
