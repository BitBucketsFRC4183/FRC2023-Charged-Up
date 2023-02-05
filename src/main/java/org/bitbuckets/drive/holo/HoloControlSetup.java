package org.bitbuckets.drive.holo;

import edu.wpi.first.math.controller.HolonomicDriveController;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.trajectory.TrapezoidProfile;
import org.bitbuckets.drive.controlsds.DriveControl;
import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.ProcessPath;
import org.bitbuckets.odometry.IOdometryControl;

public class HoloControlSetup implements ISetup<HoloControl> {

    final DriveControl drive;
    final IOdometryControl odo;

    public HoloControlSetup(DriveControl drive, IOdometryControl odo) {
        this.drive = drive;
        this.odo = odo;
    }

    @Override
    public HoloControl build(ProcessPath path) {

        //TODO find constants
        HolonomicDriveController holonomicDriveController = new HolonomicDriveController(
                new PIDController(
                        1,0,0
                ),
                new PIDController(
                        1,0,0
                ),
                new ProfiledPIDController(
                        1,0,0, new TrapezoidProfile.Constraints(1, 1)
                )
        );

        return new HoloControl(
                drive,
                odo,
                holonomicDriveController
        );
    }
}
