package org.bitbuckets.drive;

import com.pathplanner.lib.PathPlannerTrajectory;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import org.bitbuckets.OperatorInput;
import org.bitbuckets.auto.AutoFSM;
import org.bitbuckets.auto.AutoSubsystem;
import org.bitbuckets.drive.balance.BalanceControl;
import org.bitbuckets.drive.holo.HoloControl;
import org.bitbuckets.lib.core.HasLoop;
import org.bitbuckets.lib.debug.IDebuggable;
import org.bitbuckets.lib.tune.IValueTuner;
import org.bitbuckets.odometry.IOdometryControl;
import org.bitbuckets.vision.IVisionControl;

import java.util.Optional;


/**
 * tags: high priority
 * TODO this is becoming a sort of god class, some of this logic has to break out into smaller subsystems
 */
public class DriveSubsystem implements HasLoop {


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

    DriveFSM state = DriveFSM.IDLE;

    Optional<Pose3d> visionTarget;

    @Override
    public void loop() {

        if (state == DriveFSM.IDLE) {
            if (autoSubsystem.state() == AutoFSM.AUTO_RUN) {
                state = DriveFSM.AUTO_PATHFINDING;
            }
            if (autoSubsystem.state() == AutoFSM.TELEOP) {
                state = DriveFSM.TELEOP_NORMAL;
            }
        } else if (state == DriveFSM.TELEOP_NORMAL && autoSubsystem.state() == AutoFSM.AUTO_RUN) {
            // switch to auto from teleop
            state = DriveFSM.AUTO_PATHFINDING;
        }

        switch (autoSubsystem.state()) {
            case AUTO_RUN -> autoLoop();
            case TELEOP -> teleopLoop();
            case DISABLED ->
                // TODO: we aren't really "uninitialized" but the drive subsystem treats uninitialized and disabled as the same?
                    state = DriveFSM.IDLE;
        }

        debuggable.log("state", state.toString());
    }

    void teleopLoop() {
        switch (state) {
            case TELEOP_NORMAL -> {
                if (input.isVisionGoPressed() && visionControl.isTargTrue()) {
                    visionTarget = visionControl.estimateVisionTargetPose();
                    state = DriveFSM.TELEOP_VISION;
                    break;
                }
                if (input.isAutoBalancePressed()) {
                    state = DriveFSM.TELEOP_BALANCING; //do balancing next iteration
                    break;
                }
                if (input.isAutoHeadingPressed()) {
                    state = DriveFSM.TELEOP_AUTOHEADING;
                    break;
                }
                teleopNormal();
            }
            case TELEOP_BALANCING -> {
                if (input.isDefaultPressed()) {
                    state = DriveFSM.TELEOP_NORMAL;
                    break;
                }
                balance();
            }
            case TELEOP_VISION -> {
                if ((!input.isVisionGoPressed())) {
                    state = DriveFSM.TELEOP_NORMAL;
                    break;
                }
                teleopVision();
            }
        }
    }

    void autoLoop() {
        switch (state) {
            case AUTO_PATHFINDING:

                if (autoSubsystem.state() == AutoFSM.TELEOP) {
                    state = DriveFSM.TELEOP_NORMAL;
                    break;
                }

                if (autoSubsystem.state() == AutoFSM.AUTO_ENDED) {
                    driveControl.drive(new ChassisSpeeds(0, 0, 0));
                    break;

                }
                Optional<PathPlannerTrajectory.PathPlannerState> opt = autoSubsystem.samplePathPlannerState();
                if (opt.isPresent()) {
                    ChassisSpeeds targetSpeeds = holoControl.calculatePose2DFromState(opt.get());
                    targetSpeeds.vxMetersPerSecond = -targetSpeeds.vxMetersPerSecond;
                    targetSpeeds.vyMetersPerSecond = -targetSpeeds.vyMetersPerSecond;
                    targetSpeeds.omegaRadiansPerSecond = -targetSpeeds.omegaRadiansPerSecond;

                    driveControl.drive(targetSpeeds);
                }


                if (autoSubsystem.sampleHasEventStarted("auto-balance")) {
                    state = DriveFSM.AUTO_BALANCING;
                    break;
                }
                break;

            case AUTO_BALANCING:
                balance();
                break;
        }
    }

    void teleopVision() {
        if (visionTarget.isPresent()) {
            ChassisSpeeds speeds = holoControl.calculatePose2D(visionTarget.get().toPose2d(), 1, visionTarget.get().toPose2d().getRotation());
            speeds.vxMetersPerSecond = -speeds.vxMetersPerSecond;
            speeds.vyMetersPerSecond = -speeds.vyMetersPerSecond;
            speeds.omegaRadiansPerSecond = -speeds.omegaRadiansPerSecond;


            driveControl.drive(speeds);
        }


    }

    void teleopNormal() {
        if (input.isResetGyroPressed()) {
            odometryControl.zero();
        }
        if (input.isResetOdoPressed()) {
            odometryControl.setPos(Rotation2d.fromDegrees(0), driveControl.currentPositions(), new Pose2d(0, 0, Rotation2d.fromDegrees(0)));
        }

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
        debuggable.log("x-output", xOutput);
        debuggable.log("y-output", yOutput);
        debuggable.log("rot-output", rotationOutput);

        switch (orientation.readValue()) {
            case FIELD_ORIENTED:
                if (xOutput == 0 && yOutput == 0 && rotationOutput == 0) {
                    driveControl.stop();
                } else {
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

    void balance() {

        debuggable.log("is-running-ab", true);
        //This is bad and should be shifted somewhere else

        double Pitch_deg = odometryControl.getPitch_deg();

        debuggable.log("pitch-now", Pitch_deg);
        if (Math.abs(Pitch_deg) > 0.1) {
            debuggable.log("is-running-ab-2", true);

            double output = balanceControl.calculateBalanceOutput(Pitch_deg, 0);

            debuggable.log("control-output-autobalance", output);

            driveControl.drive(new ChassisSpeeds(output / 2, 0.0, 0.0));
        } else {
            driveControl.stop();

        }
    }


}
