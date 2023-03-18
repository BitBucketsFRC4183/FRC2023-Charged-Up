package org.bitbuckets.drive;

import com.pathplanner.lib.PathPlannerTrajectory;
import config.Drive;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import org.bitbuckets.OperatorInput;
import org.bitbuckets.auto.AutoSubsystem;
import org.bitbuckets.drive.balance.BalanceControl;
import org.bitbuckets.drive.holo.HoloControl;
import org.bitbuckets.lib.core.HasLifecycle;
import org.bitbuckets.lib.core.HasLoop;
import org.bitbuckets.lib.log.IDebuggable;
import org.bitbuckets.lib.tune.IValueTuner;
import org.bitbuckets.odometry.IOdometryControl;
import org.bitbuckets.vision.IVisionControl;

import java.util.Optional;


/**
 * tags: high priority
 * TODO this is becoming a sort of god class, some of this logic has to break out into smaller subsystems
 */
public class DriveSubsystem implements HasLoop, HasLifecycle {


    final OperatorInput input;
    final AutoSubsystem autoSubsystem;
    final IOdometryControl odometryControl;
    final BalanceControl balanceControl;
    final IDriveControl driveControl;
    final HoloControl holoControl;
    final IVisionControl visionControl;
    final IValueTuner<OrientationChooser> orientation;
    final IDebuggable debuggable;

    public enum OrientationChooser {
        FIELD_ORIENTED,
        ROBOT_ORIENTED,
    }

    public DriveSubsystem(OperatorInput input, IOdometryControl odometryControl, BalanceControl balanceControl, IDriveControl driveControl, AutoSubsystem autoSubsystem, HoloControl holoControl, IVisionControl visionControl, IValueTuner<OrientationChooser> orientation, IDebuggable debuggable) {
        this.input = input;
        this.odometryControl = odometryControl;
        this.balanceControl = balanceControl;
        this.driveControl = driveControl;
        this.autoSubsystem = autoSubsystem;
        this.visionControl = visionControl;
        this.orientation = orientation;
        this.holoControl = holoControl;
        this.debuggable = debuggable;
    }


    @Override
    public void loop() {
        if (input.isResetGyroPressed()) {
            odometryControl.zero();
        }

        handleLogic();


        debuggable.log("state", nextStateShould.toString());
        debuggable.log("stop", shouldStop);

    }

    DriveFSM nextStateShould = DriveFSM.IDLE;

    @Override
    public void autonomousPeriodic() {
        if (autoSubsystem.sampleHasEventStarted("auto-balance")) {
            nextStateShould = DriveFSM.BALANCE;
        } else if (autoSubsystem.sampleHasEventStarted("do-vision")) {
            nextStateShould = DriveFSM.VISION;
        } else {
            nextStateShould = DriveFSM.AUTO_PATHFINDING;
        }
    }

    @Override
    public void teleopPeriodic() {
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
    }

    @Override
    public void teleopInit() {
        nextStateShould = DriveFSM.MANUAL;
    }

    @Override
    public void autonomousInit() {
        nextStateShould = DriveFSM.AUTO_PATHFINDING;
    }

    @Override
    public void disabledInit() {
        nextStateShould = DriveFSM.IDLE;
    }

    @Override
    public void disabledPeriodic() {
        nextStateShould = DriveFSM.IDLE;
    }

    void handleLogic() {
        if (nextStateShould == DriveFSM.AUTO_PATHFINDING) {
            autoPathFinding();
            return;
        }

        if (nextStateShould == DriveFSM.MANUAL) {
            teleopNormal();
            return;
        }

        if (nextStateShould == DriveFSM.BALANCE) {
            balance();
            return;
        }

        if (nextStateShould == DriveFSM.VISION) {
            teleopVision();
            return;

        }


        if (nextStateShould == DriveFSM.IDLE) {
            driveControl.stop();
        }

        //if idle it will do nothing..
    }


    void autoPathFinding() {
        Optional<PathPlannerTrajectory.PathPlannerState> opt = autoSubsystem.samplePathPlannerState();
        if (opt.isPresent()) {
            PathPlannerTrajectory.PathPlannerState state = opt.get();

            Pose2d filtered = new Pose2d(
                    state.poseMeters.getTranslation(),
                    state.holonomicRotation
            );

            ChassisSpeeds targetSpeeds = holoControl.calculatePose2D(filtered, state.velocityMetersPerSecond);
            targetSpeeds.vxMetersPerSecond = -targetSpeeds.vxMetersPerSecond;
            targetSpeeds.vyMetersPerSecond = -targetSpeeds.vyMetersPerSecond;
            targetSpeeds.omegaRadiansPerSecond = -targetSpeeds.omegaRadiansPerSecond;

            driveControl.drive(targetSpeeds);
        } else {
            driveControl.stop();
        }
    }


    void teleopVision() {

        var targetPose = visionControl.estimateBestVisionTarget();
        if (targetPose.isPresent()) {
            ChassisSpeeds speeds = holoControl.calculatePose2D(targetPose.get().toPose2d(), 1);

            ChassisSpeeds inverted = new ChassisSpeeds(
                    -speeds.vxMetersPerSecond,
                    -speeds.vyMetersPerSecond,
                    -speeds.omegaRadiansPerSecond
            );//TODO fix this i have no idea why it works

            driveControl.drive(inverted);
        }
    }

    void teleopNormal() {


        double xOutput;
        double yOutput;
        double rotationOutput;

        if (input.isSlowDrivePressed()) {
            xOutput = input.getInputX() * driveControl.getMaxVelocity() * 0.1;
            yOutput = -input.getInputY() * driveControl.getMaxVelocity() * 0.1;
            rotationOutput = input.getInputRot() * driveControl.getMaxAngularVelocity() * 0.1;

        } else {
            xOutput = input.getInputX() * driveControl.getMaxVelocity();
            yOutput = -input.getInputY() * driveControl.getMaxVelocity();
            rotationOutput = input.getInputRot() * driveControl.getMaxAngularVelocity();
        }

        if (input.stopStickyPressed()) {
            driveControl.stop();
        }

        debuggable.log("x-output", xOutput);
        debuggable.log("y-output", yOutput);
        debuggable.log("rot-output", rotationOutput);

        switch (orientation.readValue()) {
            case FIELD_ORIENTED:
                if (xOutput == 0 && yOutput == 0 && rotationOutput == 0) {
                    driveControl.stop();
                } else {
                    debuggable.log("y", yOutput);
                    debuggable.log("x", xOutput);

                    driveControl.drive(
                            ChassisSpeeds.fromFieldRelativeSpeeds(yOutput, xOutput, rotationOutput, odometryControl.getRotation2d())
                    );
                }
                break;
            case ROBOT_ORIENTED:
                if (xOutput == 0 && yOutput == 0 && rotationOutput == 0) {
                    driveControl.stop();
                } else {
                    ChassisSpeeds robotOrient = new ChassisSpeeds(xOutput, yOutput, rotationOutput);
                    driveControl.drive(robotOrient);
                }
                break;
        }

    }

    boolean shouldStop = false;

    void balance() {
        double Pitch_deg = odometryControl.getPitch_deg();

        debuggable.log("pitch-now", Pitch_deg);
        debuggable.log("accel", odometryControl.getAccelerationZ());
        if (Math.abs(Pitch_deg) > 2) {

            if (shouldStop) {
                driveControl.drive(new ChassisSpeeds(0, 0, 0));
                return;
            }

            if (odometryControl.getAccelerationZ() > Drive.ACCEL_THRESHOLD_AUTOBALANCE) {
                shouldStop = true;
                System.out.println("AAAAAAAAAAAAAAA");

                driveControl.drive(new ChassisSpeeds(Math.signum(Pitch_deg) * 1.80, 0, 0));
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
