package org.bitbuckets.auto;

import org.bitbuckets.lib.hardware.PIDIndex;

/**
 * labels: low priority, low difficulty
 * TODO: This should use PidConstants and not PidIndex
 */
public interface AutoConstants {
    double maxPathFollowVelocity = 1.25;
    double maxPathFollowAcceleration = 2.25;

    double[] pathXYPID = PIDIndex.CONSTANTS(3.2416, 0, 0.1, 0, 0);
    double[] pathThetaPID = PIDIndex.CONSTANTS(3, 0, 0.02, 0, 0);

}
