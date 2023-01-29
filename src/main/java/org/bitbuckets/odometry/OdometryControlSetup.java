package org.bitbuckets.odometry;

import edu.wpi.first.wpilibj.smartdashboard.Field2d;
import org.bitbuckets.drive.IDriveControl;
import org.bitbuckets.drive.controlsds.DriveControlSDS;
import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.ProcessPath;

public class OdometryControlSetup implements ISetup<OdometryControl> {


    final IDriveControl control;

    final DriveControlSDS driveControlSDS;

    final Field2d field2d;


    public OdometryControlSetup(IDriveControl control, DriveControlSDS driveControlSDS, Field2d field2d) {
        this.control = control;
        this.driveControlSDS = driveControlSDS;
        this.field2d = field2d;
    }

    @Override
    public OdometryControl build(ProcessPath path) {
        return new OdometryControl(control, driveControlSDS, field2d.getRobotPose());
    }
}
