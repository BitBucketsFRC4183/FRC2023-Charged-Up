package org.bitbuckets.drive.holo;

import edu.wpi.first.math.controller.HolonomicDriveController;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.trajectory.TrapezoidProfile;
import org.bitbuckets.drive.controlsds.DriveControl;
import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.ProcessPath;
import org.bitbuckets.lib.control.*;
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
    public HoloControl build(ProcessPath self) {

        IPIDCalculator x = new PIDCalculatorSetup(new PIDConfig(1.2,0,0,0))
                .build(self.addChild("x-control"));

        IPIDCalculator y = new PIDCalculatorSetup(new PIDConfig(1.2, 0, 0, 0))
                .build(self.addChild("y-control"));

        IPIDCalculator theta = new ProfiledPIDFSetup(new PIDConfig(0.5,0,0,0), new TrapezoidProfile.Constraints(1,1)).build(self.addChild("theta-control"));

        //TODO find constants
        HolonomicDriveController holonomicDriveController = new HolonomicDriveController(
                x.rawAccess(PIDController.class),
                y.rawAccess(PIDController.class),
                theta.rawAccess(ProfiledPIDController.class)
        );
        var debuggable = self.generateDebugger();


        return new HoloControl(
                drive,
                visionControl,
                odo,
                holonomicDriveController,
                debuggable);
    }
}
