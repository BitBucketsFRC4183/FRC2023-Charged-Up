package org.bitbuckets.odometry;

import edu.wpi.first.wpilibj.smartdashboard.Field2d;
import org.bitbuckets.drive.controlsds.DriveControl;
import org.bitbuckets.odometry.OdometryControl;

public class FieldLoggingAspect implements Runnable {

    final Field2d field2d;
    final OdometryControl control;

    public FieldLoggingAspect(Field2d field2d, OdometryControl control) {
        this.field2d = field2d;
        this.control = control;
    }

    @Override
    public void run() {
        field2d.setRobotPose(
                control.estimatedSwervePose()
        );
    }
}
