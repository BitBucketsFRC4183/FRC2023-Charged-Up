package org.bitbuckets.drive;

import com.pathplanner.lib.PathPlannerTrajectory;
import config.Drive;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import org.bitbuckets.OperatorInput;
import org.bitbuckets.auto.AutoSubsystem;
import org.bitbuckets.drive.balance.BalanceControl;
import org.bitbuckets.drive.holo.HoloControl;
import org.bitbuckets.lib.control.IPIDCalculator;
import org.bitbuckets.lib.core.HasLifecycle;
import org.bitbuckets.lib.core.HasLogLoop;
import org.bitbuckets.lib.log.IDebuggable;
import org.bitbuckets.lib.tune.IValueTuner;
import org.bitbuckets.odometry.IOdometryControl;
import org.bitbuckets.vision.IVisionControl;

import java.util.Optional;


/**
 * tags: high priority
 * TODO this is becoming a sort of god class, some of this logic has to break out into smaller subsystems
 */
public class DriveSubsystem implements HasLifecycle, HasLogLoop {


    final OperatorInput input;
    final AutoSubsystem autoSubsystem;
    final IOdometryControl odometryControl;
    final BalanceControl balanceControl;
    final IDriveControl driveControl;
    final HoloControl holoControl;
    final IVisionControl visionControl;
    final IValueTuner<OrientationChooser> orientation;
    final IDebuggable debuggable;
    final IPIDCalculator waitTimeResponse;

    public enum OrientationChooser {
        FIELD_ORIENTED,
        ROBOT_ORIENTED,
    }

    public DriveSubsystem(OperatorInput input, IOdometryControl odometryControl, BalanceControl balanceControl, IDriveControl driveControl, AutoSubsystem autoSubsystem, HoloControl holoControl, IVisionControl visionControl, IValueTuner<OrientationChooser> orientation, IDebuggable debuggable, IPIDCalculator waitTimeResponse) {
        this.input = input;
        this.odometryControl = odometryControl;
        this.balanceControl = balanceControl;
        this.driveControl = driveControl;
        this.autoSubsystem = autoSubsystem;
        this.visionControl = visionControl;
        this.orientation = orientation;
        this.holoControl = holoControl;
        this.debuggable = debuggable;
        this.waitTimeResponse = waitTimeResponse;
    }

    @Override
    public void logLoop() {
        debuggable.log("state", nextStateShould.toString());
        debuggable.log("stop", shouldStop);
    }

    DriveFSM nextStateShould = DriveFSM.IDLE;

    @Override
    public void autonomousPeriodic() {
        // auto mode will balance at the end, stop when the path is done, or do path finding by default
        if (autoSubsystem.sampleHasEventStarted("auto-balance")) {
            nextStateShould = DriveFSM.BALANCE;
            balance();
        } else if (autoSubsystem.isPathDone()) {
            nextStateShould = DriveFSM.IDLE;
            driveControl.stop();
        } else {
            autoPathFinding();
        }

    }

    @Override
    public void teleopPeriodic() {

        if (input.isResetGyroPressed()) {
            odometryControl.zeroOdo();
        }

        if (input.isVisionDrivePressed() && visionControl.estimateBestVisionTarget().isPresent()) {
            nextStateShould = DriveFSM.VISION;
        } else if (nextStateShould == DriveFSM.VISION && !input.isVisionDrivePressed()) {
            nextStateShould = DriveFSM.MANUAL;
        } else if (input.isManualDrivePressed()) {
            nextStateShould = DriveFSM.MANUAL;
        } else if (input.isAutoBalancePressed()) {
            nextStateShould = DriveFSM.BALANCE;
        } else if (nextStateShould == DriveFSM.BALANCE && !input.isAutoBalancePressed()) {
            shouldStop = false;
            nextStateShould = DriveFSM.MANUAL;
        }

        // call based on state
        switch (nextStateShould) {
            case MANUAL -> teleopNormal();
            case BALANCE -> balance();
            case VISION -> teleopVision();
            default -> driveControl.stop();
        }
    }

    @Override
    public void teleopInit() {
        nextStateShould = DriveFSM.MANUAL;
    }

    @Override
    public void disabledInit() {
        nextStateShould = DriveFSM.IDLE;
        driveControl.stop();
    }

    @Override
    public void disabledPeriodic() {
        nextStateShould = DriveFSM.IDLE;
        driveControl.stop();
    }


    void autoPathFinding() {
        Optional<PathPlannerTrajectory.PathPlannerState> opt = autoSubsystem.samplePathPlannerState();
        if (opt.isPresent()) {
            PathPlannerTrajectory.PathPlannerState state = opt.get();
            ChassisSpeeds targetSpeeds = holoControl.calculatePose2D(state.poseMeters, state.holonomicRotation, state.velocityMetersPerSecond);

            driveControl.drive(targetSpeeds);
        } else {
            driveControl.stop();
        }
    }


    void teleopVision() {

        var targetPose = visionControl.estimateBestVisionTarget();
        if (targetPose.isPresent()) {
            ChassisSpeeds speeds = holoControl.calculatePose2D(targetPose.get().toPose2d(), targetPose.get().toPose2d().getRotation().plus(Rotation2d.fromDegrees(180)), 1);

            driveControl.drive(speeds);
        }
    }

    void teleopNormal() {
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

        debuggable.log("x-output", leftSpeed);
        debuggable.log("y-output", forwardSpeed);
        debuggable.log("rot-output", rotationOutput);

        switch (orientation.readValue()) {
            case FIELD_ORIENTED:
                if (leftSpeed == 0 && forwardSpeed == 0 && rotationOutput == 0) {
                    driveControl.stop();
                } else {
                    debuggable.log("y", forwardSpeed);
                    debuggable.log("x", leftSpeed);

                    driveControl.drive(
                            ChassisSpeeds.fromFieldRelativeSpeeds(forwardSpeed, leftSpeed, rotationOutput, odometryControl.getRotation2d())
                    );
                }
                break;
            case ROBOT_ORIENTED:
                if (leftSpeed == 0 && forwardSpeed == 0 && rotationOutput == 0) {
                    driveControl.stop();
                } else {
                    ChassisSpeeds robotOrient = new ChassisSpeeds(forwardSpeed, leftSpeed, rotationOutput);
                    driveControl.drive(robotOrient);
                }
                break;
        }

    }

    boolean shouldStop = false;
    int dumbCounter = 0;

    void balance() {
        double Pitch_deg = odometryControl.getPitch_deg();

        debuggable.log("pitch-now", Pitch_deg);
        debuggable.log("accel", odometryControl.getAccelerationZ());
        if (Math.abs(Pitch_deg) > 2) {

            if (shouldStop) {
                double waitFB = waitTimeResponse.calculateNext(odometryControl.getYaw_deg(), 0);
                double adjustedTime = 40 + waitFB;

                debuggable.log("fb-alone", waitFB);
                debuggable.log("fb-adjusted-time", adjustedTime);

                if (dumbCounter > 50) {
                    dumbCounter = 0;
                    driveControl.drive(new ChassisSpeeds(0, 0, 0));

                } else {
                    dumbCounter++;
                }
                return;
            }

            if (odometryControl.getAccelerationZ() > Drive.ACCEL_THRESHOLD_AUTOBALANCE) {
                shouldStop = true;
                System.out.println("AAAAAAAAAAAAAAA");

                driveControl.drive(new ChassisSpeeds(Math.signum(Pitch_deg) * 0.7, 0, 0));
                return;
            } else {
                double output = balanceControl.calculateBalanceOutput(Pitch_deg, 0);
                debuggable.log("control-output-autobalance", output);

                driveControl.drive(new ChassisSpeeds(-output, 0.0, 0.0));
            }


        } else {
            debuggable.log("is-running-ab-2", false);

            driveControl.stop();

        }
    }


}
