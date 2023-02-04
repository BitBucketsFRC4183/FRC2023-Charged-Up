package org.bitbuckets.odometry;

import edu.wpi.first.math.geometry.Pose2d;
//import org.bitbuckets.lib.log.Loggable;


public class OdometryData {

    final OdometryControl odometryControl;




    public OdometryData(OdometryControl odometryControl) {
        this.odometryControl = odometryControl;

        Pose2d currentEstimatedPosition = odometryControl.estimatedSwervePose();
    }
}
