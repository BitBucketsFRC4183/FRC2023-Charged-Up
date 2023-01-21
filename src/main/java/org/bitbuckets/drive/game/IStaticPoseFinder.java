package org.bitbuckets.drive.game;

import edu.wpi.first.math.geometry.Pose3d;

public interface IStaticPoseFinder {

    boolean isRedOrBlue();

    Pose3d startingPose();
    Pose3d someOtherPose();

}
