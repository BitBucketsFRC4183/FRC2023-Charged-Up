package org.bitbuckets.drive;

import com.pathplanner.lib.PathPlannerTrajectory;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj.Preferences;
import org.bitbuckets.auto.AutoFSM;
import org.bitbuckets.auto.AutoSubsystem;
import org.bitbuckets.drive.balance.ClosedLoopsControl;
import org.bitbuckets.drive.controlsds.DriveControl;
import org.bitbuckets.drive.holo.HoloControl;
import org.bitbuckets.lib.log.Debuggable;
import org.bitbuckets.lib.tune.IValueTuner;
import org.bitbuckets.odometry.IOdometryControl;
import org.bitbuckets.vision.IVisionControl;

import java.util.Optional;


/**
 * tags: high priority
 * TODO this is becoming a sort of god class, some of this logic has to break out into smaller subsystems
 */
public class DriveSubsystem {

    final DriveInput input;
    final AutoSubsystem autoSubsystem;
    final IOdometryControl odometryControl;
    final ClosedLoopsControl closedLoopsControl;
    final DriveControl driveControl;
    final HoloControl holoControl;
    final IVisionControl visionControl;
    final IValueTuner<OrientationChooser> orientation;
    final Debuggable debuggable;


    public enum OrientationChooser {
        FIELD_ORIENTED,
        ROBOT_ORIENTED,
    }


    public DriveSubsystem(DriveInput input, IOdometryControl odometryControl, ClosedLoopsControl closedLoopsControl, DriveControl driveControl, AutoSubsystem autoSubsystem, HoloControl holoControl, IVisionControl visionControl, IValueTuner<OrientationChooser> orientation, Debuggable debuggable) {
        this.input = input;
        this.odometryControl = odometryControl;
        this.closedLoopsControl = closedLoopsControl;
        this.driveControl = driveControl;
        this.autoSubsystem = autoSubsystem;
        this.visionControl = visionControl;
        this.orientation = orientation;
        this.holoControl = holoControl;
        this.debuggable = debuggable;
    }

    DriveFSM state = DriveFSM.UNINITIALIZED;

    Optional<Pose3d> visionTarget;

    public void runLoop() {

        if (state == DriveFSM.UNINITIALIZED) {
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

            case AUTO_RUN:
                autoLoop();
                break;
            case TELEOP:
                teleopLoop();
                break;
            case DISABLED:
                // TODO: we aren't really "uninitialized" but the drive subsystem treats uninitialized and disabled as the same?
                state = DriveFSM.UNINITIALIZED;
                break;
        }

        debuggable.log("state", state.toString());
    }

    void teleopLoop() {
        switch (state) {
            case TELEOP_NORMAL:
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
                break;

            case TELEOP_BALANCING:
                if (input.isDefaultPressed()) {
                    state = DriveFSM.TELEOP_NORMAL;
                    break;
                }
                balance();
                break;

            case TELEOP_VISION:
                if ((!input.isVisionGoPressed())) {
                    state = DriveFSM.TELEOP_NORMAL;
                    break;
                }
                teleopVision();
                break;

            case TELEOP_AUTOHEADING:
                if (input.isDefaultPressed()) {
                    state = DriveFSM.TELEOP_NORMAL;
                    break;
                }
                teleopAutoheading();
                break;
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
        } else {
            ChassisSpeeds speeds = new ChassisSpeeds(0, 0, 0);
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

        if (input.isSlowDriveHeld()) {
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
                    driveControl.stopSticky();
                } else {
                    driveControl.drive(
                            ChassisSpeeds.fromFieldRelativeSpeeds(yOutput, xOutput, rotationOutput, odometryControl.getRotation2d())
                    );
                }
                break;
            case ROBOT_ORIENTED:
                if (xOutput == 0 && yOutput == 0 && rotationOutput == 0) {
                    driveControl.stopSticky();
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
        double BalanceDeadband_deg = Preferences.getDouble(DriveConstants.autoBalanceDeadbandDegKey, DriveConstants.BalanceDeadbandDeg);
        double Pitch_deg = odometryControl.getPitch_deg();

        debuggable.log("pitch-now", Pitch_deg);
        if (Math.abs(Pitch_deg) > BalanceDeadband_deg) {
            debuggable.log("is-running-ab-2", true);

            double output = closedLoopsControl.calculateBalanceOutput(Pitch_deg, 0);

            debuggable.log("control-output-autobalance", output);

            driveControl.drive(new ChassisSpeeds(output / 2, 0.0, 0.0));
        } else {
            driveControl.stop90degrees();

        }
    }

    void teleopAutoheading() {
        double IMU_Yaw = Math.toRadians(odometryControl.getYaw_deg());//Math.toRadians(-350);

        //will add logic later
        double setpoint = Math.toRadians(0);
        double error = setpoint - IMU_Yaw;

        double rotationOutputOrient = closedLoopsControl.calculateRotOutputRad(
                IMU_Yaw,
                setpoint
        );
//        if(Math.abs(error) < 180)
//        {
//            rotationOutput = -rotationOutput;
//        }
        if (Math.abs(error) > Math.toRadians(2)) {
            driveControl.drive(
                    new ChassisSpeeds(0, 0, rotationOutputOrient)
            );
        } else {
            driveControl.stop();
        }
    }
}
