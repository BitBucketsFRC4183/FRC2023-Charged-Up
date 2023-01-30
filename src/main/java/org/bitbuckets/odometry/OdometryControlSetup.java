package org.bitbuckets.odometry;

import edu.wpi.first.math.geometry.Pose2d;
import org.bitbuckets.drive.controlsds.DriveControl;
import org.bitbuckets.gyro.GyroControl;
import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.ProcessPath;
import org.bitbuckets.vision.VisionControl;

public class OdometryControlSetup implements ISetup<OdometryControl> {


    final DriveControl control;

    final VisionControl visionControl;

    final GyroControl gyroControl;

    final Pose2d pose2d;

    public OdometryControlSetup(DriveControl control, VisionControl visionControl, GyroControl gyroControl, Pose2d pose2d) {
        this.control = control;
        this.visionControl = visionControl;
        this.gyroControl = gyroControl;
        this.pose2d = pose2d;
    }


    @Override
    public OdometryControl build(ProcessPath addChild) {
        OdometryControl odometryControl = new OdometryControl(control, visionControl, gyroControl, pose2d);
        ;

        addChild.registerLoop(odometryControl, "odometry-loop");

        return odometryControl;
    }
}
