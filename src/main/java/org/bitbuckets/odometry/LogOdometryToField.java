package org.bitbuckets.odometry;

import edu.wpi.first.wpilibj.smartdashboard.Field2d;

public class LogOdometryToField implements Runnable {

    final Field2d field2d;
    final IOdometryControl control;

    public LogOdometryToField(Field2d field2d, IOdometryControl control) {
        this.field2d = field2d;
        this.control = control;
    }

    @Override
    public void run() {
        field2d.setRobotPose(
                control.estimateFusedPose2d()
        );
    }
}
