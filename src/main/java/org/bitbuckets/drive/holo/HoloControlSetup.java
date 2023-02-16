package org.bitbuckets.drive.holo;

import edu.wpi.first.math.controller.HolonomicDriveController;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.trajectory.TrapezoidProfile;
import org.bitbuckets.drive.controlsds.DriveControl;
import org.bitbuckets.lib.IProcess;
import org.bitbuckets.lib.ISetup;
import org.bitbuckets.odometry.IOdometryControl;
import org.bitbuckets.vision.IVisionControl;

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
    public HoloControl build(IProcess self) {

        //TODO find constants
        HolonomicDriveController holonomicDriveController = new HolonomicDriveController(
                new PIDController(
                        1.5, 0, 0
                ),
                new PIDController(
                        1.5, 0, 0
                ),
                new ProfiledPIDController(
                        3, 0, 0, new TrapezoidProfile.Constraints(drive.getMaxAngularVelocity()/20, drive.getMaxAngularVelocity() * 10)
                )
        );
        var debuggable = self.getDebuggable();


        return new HoloControl(
                drive,
                visionControl,
                odo,
                holonomicDriveController,
                debuggable);
    }
}
