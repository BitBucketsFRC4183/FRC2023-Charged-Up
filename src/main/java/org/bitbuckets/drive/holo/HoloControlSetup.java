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
import org.bitbuckets.lib.control.ProfiledPIDFCalculatorSetup;
import org.bitbuckets.odometry.IOdometryControl;

public class HoloControlSetup implements ISetup<HoloControl> {

    final IDriveControl drive;
    final IOdometryControl odo;

    final PIDConfig x_pid;
    final PIDConfig y_pid;
    final PIDConfig theta_pid;
    final TrapezoidProfile.Constraints theta_constraints;

    public HoloControlSetup(IDriveControl drive, IOdometryControl odo, PIDConfig x_pid, PIDConfig y_pid, PIDConfig theta_pid, TrapezoidProfile.Constraints theta_constraints) {
        this.drive = drive;
        this.odo = odo;
        this.x_pid = x_pid;
        this.y_pid = y_pid;
        this.theta_pid = theta_pid;
        this.theta_constraints = theta_constraints;
    }



    @Override
    public HoloControl build(IProcess self) {

        IPIDCalculator x = self.childSetup("x-pid", new PIDCalculatorSetup(x_pid));
        IPIDCalculator y = self.childSetup("y-pid", new PIDCalculatorSetup(y_pid));
        IPIDCalculator theta = self.childSetup("theta-pid", new ProfiledPIDFCalculatorSetup(theta_pid, theta_constraints));

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
