package org.bitbuckets.drive.holo;

import edu.wpi.first.math.controller.HolonomicDriveController;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.trajectory.TrapezoidProfile;
import org.bitbuckets.drive.controlsds.DriveControl;
import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.ProcessPath;
import org.bitbuckets.odometry.IOdometryControl;
import org.bitbuckets.vision.IVisionControl;
import org.bitbuckets.vision.VisionControl;

public class HoloControlSetup implements ISetup<HoloControl> {

    final DriveControl drive;
    final IVisionControl visionControl;
    final IOdometryControl odo;

    public HoloControlSetup(DriveControl drive, IVisionControl visionControl, IOdometryControl odo) {
        this.drive = drive;
        this.visionControl = visionControl;
        this.odo = odo;
    }

    @Override
    public HoloControl build(ProcessPath self) {

        //TODO find constants
        HolonomicDriveController holonomicDriveController = new HolonomicDriveController(
                new PIDController(
                        .3, 0, 0
                ),
                new PIDController(
                        .3, 0, 0
                ),
                new ProfiledPIDController(
                        .3, 0, 0, new TrapezoidProfile.Constraints(1, 1)
                )
        );

        return new HoloControl(
                drive,
                visionControl,
                odo,
                holonomicDriveController
        );
    }
}
