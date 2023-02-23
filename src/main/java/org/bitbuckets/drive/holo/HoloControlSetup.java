package org.bitbuckets.drive.holo;

import edu.wpi.first.math.controller.HolonomicDriveController;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.trajectory.TrapezoidProfile;
import org.bitbuckets.drive.controlsds.DriveControl;
import org.bitbuckets.lib.IProcess;
import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.control.IPIDCalculator;
import org.bitbuckets.lib.control.PIDCalculatorSetup;
import org.bitbuckets.lib.control.PIDConfig;
import org.bitbuckets.lib.control.ProfiledPIDFSetup;
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

    PIDConfig X_PID = new PIDConfig(1.2,0,0,0, continuousMin, continuousMax);
    PIDConfig Y_PID = new PIDConfig(1.2,0,0,0, continuousMin, continuousMax);
    PIDConfig THETA_PID = new PIDConfig(1,0,0,0, continuousMin, continuousMax);
    TrapezoidProfile.Constraints THETA_CONSTRAINTS = new TrapezoidProfile.Constraints(1,1);

    @Override
    public HoloControl build(IProcess self) {

        IPIDCalculator x = self.childSetup("x-pid", new PIDCalculatorSetup(X_PID));
        IPIDCalculator y = self.childSetup("y-pid", new PIDCalculatorSetup(Y_PID));
        IPIDCalculator theta = self.childSetup("theta-pid", new ProfiledPIDFSetup(THETA_PID, THETA_CONSTRAINTS));

        HolonomicDriveController holonomicDriveController = new HolonomicDriveController(
                x.rawAccess(PIDController.class),
                y.rawAccess(PIDController.class),
                theta.rawAccess(ProfiledPIDController.class)
        );
        holonomicDriveController.setTolerance(
                new Pose2d(0.1, 0.1, Rotation2d.fromDegrees(1))
        );


        return new HoloControl(
                drive,
                visionControl,
                odo,
                holonomicDriveController,
                self.getDebuggable());
    }
}
