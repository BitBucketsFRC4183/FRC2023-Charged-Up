package org.bitbuckets.drive.module;

import org.bitbuckets.lib.hardware.PIDIndex;

public interface AutoConstants {
    public double maxPathFollowVelocity = 1.25;
         double maxPathFollowAcceleration = 2.25;

         double[] pathXYPID = PIDIndex.CONSTANTS(3.2416, 0, 0.1, 0, 0);
         double[] pathThetaPID = PIDIndex.CONSTANTS(3, 0, 0.02, 0, 0);

    }

