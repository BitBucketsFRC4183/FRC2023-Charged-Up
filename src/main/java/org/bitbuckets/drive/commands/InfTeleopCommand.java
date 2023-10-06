package org.bitbuckets.drive.commands;

import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj2.command.CommandBase;
import org.bitbuckets.OperatorInput;
import org.bitbuckets.drive.IDriveControl;
import org.bitbuckets.lib.log.IDebuggable;
import org.bitbuckets.odometry.IOdometryControl;

public class InfTeleopCommand extends CommandBase {

    final OperatorInput input;
    final IDriveControl driveControl;
    final IOdometryControl odometryControl;
    final IDebuggable debuggable;

    interface Component {
        void logX(double x);
        void logY(double y);
        void logRot(double rot);
    }

    public InfTeleopCommand(OperatorInput input, IDriveControl driveControl, IOdometryControl odometryControl, IDebuggable debuggable) {
        this.input = input;
        this.driveControl = driveControl;
        this.odometryControl = odometryControl;
        this.debuggable = debuggable;

        addRequirements(driveControl);
    }

    @Override
    public void execute() {
        // get the operator inputs
        // "forward" (away from alliance wall, positive along the x axis)
        // "left" (positive y axis, left from the perspective of the driver)
        // rotation output, (positive means spin left)
        double forwardSpeed;
        double leftSpeed;
        double rotationOutput;

        if (input.isSlowDrivePressed()) {
            forwardSpeed = input.getInputForward() * driveControl.getMaxVelocity() * 0.1;
            leftSpeed = input.getInputLeft() * driveControl.getMaxVelocity() * 0.1;
            rotationOutput = input.getInputRot() * driveControl.getMaxAngularVelocity() * 0.1;

        } else {
            forwardSpeed = input.getInputForward() * driveControl.getMaxVelocity();
            leftSpeed = input.getInputLeft() * driveControl.getMaxVelocity();
            rotationOutput = input.getInputRot() * driveControl.getMaxAngularVelocity();
        }

        if (input.stopStickyPressed()) {
            driveControl.stop();
        }

        debuggable.log("rot-output", rotationOutput);
        debuggable.log("y", forwardSpeed);
        debuggable.log("x", leftSpeed);


        if (leftSpeed == 0 && forwardSpeed == 0 && rotationOutput == 0) {
            driveControl.stop();
        } else {
            driveControl.drive(
                    ChassisSpeeds.fromFieldRelativeSpeeds(forwardSpeed, leftSpeed, rotationOutput, odometryControl.getRotation2d())
            );
        }
    }

    @Override
    public boolean isFinished() {
        return false; //will never complete naturally
    }

    @Override
    public InterruptionBehavior getInterruptionBehavior() {
        return InterruptionBehavior.kCancelSelf;
    }
}
