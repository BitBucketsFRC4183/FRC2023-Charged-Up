package org.bitbuckets.drive.holo;

import edu.wpi.first.math.controller.HolonomicDriveController;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.trajectory.TrapezoidProfile;
import org.bitbuckets.drive.IDriveControl;
import org.bitbuckets.lib.IProcess;
import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.control.IPIDCalculator;
import org.bitbuckets.lib.control.PIDCalculatorSetup;
import org.bitbuckets.lib.control.PIDConfig;
import org.bitbuckets.lib.control.ProfiledPIDFSetup;
import org.bitbuckets.odometry.IOdometryControl;

import java.util.Optional;

public class HoloControlSetup implements ISetup<HoloControl> {

    final IDriveControl drive;
    final IOdometryControl odo;

    public HoloControlSetup(IDriveControl drive, IOdometryControl odo) {
        this.drive = drive;
        this.odo = odo;
    }

    PIDConfig X_PID = new PIDConfig(1.2,0,0, Optional.empty(),Optional.empty());
    PIDConfig Y_PID = new PIDConfig(1.2,0,0,Optional.empty(),Optional.empty());
    PIDConfig THETA_PID = new PIDConfig(1,0,0,Optional.empty(),Optional.empty());
    TrapezoidProfile.Constraints THETA_CONSTRAINTS = new TrapezoidProfile.Constraints(1,1);

    @Override
    public HoloControl build(IProcess self) {

        IPIDCalculator x = self.childSetup("x-pid", new PIDCalculatorSetup(X_PID));
        IPIDCalculator y = self.childSetup("y-pid", new PIDCalculatorSetup(Y_PID));
        IPIDCalculator theta = self.childSetup("theta-pid", new ProfiledPIDFSetup(THETA_PID, THETA_CONSTRAINTS));

        ProfiledPIDController ctrl = theta.rawAccess(ProfiledPIDController.class);
        ctrl.enableContinuousInput(0, Math.PI * 2.0);

        HolonomicDriveController holonomicDriveController = new HolonomicDriveController(
                x.rawAccess(PIDController.class),
                y.rawAccess(PIDController.class),
                ctrl

        );

        holonomicDriveController.setTolerance(
                new Pose2d(0.1, 0.1, Rotation2d.fromDegrees(1))
        );


        return new HoloControl(
                drive,
                odo,
                holonomicDriveController,
                self.getDebuggable()
        );
    }
}
