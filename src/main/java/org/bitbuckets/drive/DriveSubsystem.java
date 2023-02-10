package org.bitbuckets.drive;

import com.pathplanner.lib.PathPlannerTrajectory;
import edu.wpi.first.math.geometry.Pose3d;
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

    public void runLoop() {
        switch (state) {
            case UNINITIALIZED:
                if (autoSubsystem.state() == AutoFSM.AUTO_RUN) {
                    state = DriveFSM.AUTO_PATHFINDING;
                    break;
                }
                if (autoSubsystem.state() == AutoFSM.TELEOP) {
                    state = DriveFSM.TELEOP_NORMAL;
                    break;
                }
                break;

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
                    driveControl.drive(targetSpeeds);
                }
                break;

            case TELEOP_NORMAL:
                if (autoSubsystem.state() == AutoFSM.AUTO_RUN) {
                    state = DriveFSM.AUTO_PATHFINDING;
                    break;
                }
                if (input.isVisionGoPressed()) {
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
                teleopBalancing();
                break;

            case TELEOP_VISION:
                if (!input.isVisionGoPressed()) {
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

    void teleopVision() {
        Optional<Pose3d> res = visionControl.estimateTargetPose();
        if (res.isEmpty()) return;
        ChassisSpeeds speeds = holoControl.calculatePose2D(res.get().toPose2d(), 3, res.get().toPose2d().getRotation());

        driveControl.drive(speeds);
    }

    void teleopNormal() {
        if (input.isResetGyroPressed()) {
            odometryControl.zero();
        }

        double xOutput = input.getInputX() * driveControl.getMaxVelocity();
        double yOutput = -input.getInputY() * driveControl.getMaxVelocity();
        double rotationOutput = input.getInputRot() * driveControl.getMaxAngularVelocity();

        debuggable.log("x-output", xOutput);
        debuggable.log("y-output", yOutput);
        debuggable.log("rot-output", rotationOutput);

        switch (orientation.readValue()) {
            case FIELD_ORIENTED:
                if (xOutput == 0 && yOutput == 0 && rotationOutput == 0) {
                    driveControl.stopSticky();
                } else {
                    driveControl.drive(
                            ChassisSpeeds.fromFieldRelativeSpeeds(xOutput, yOutput, rotationOutput, odometryControl.getRotation2d())
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
        debuggable.log("state", state);

    }

    void teleopBalancing() {

        //This is bad and should be shifted somewhere else
        double BalanceDeadband_deg = Preferences.getDouble(DriveConstants.autoBalanceDeadbandDegKey, DriveConstants.BalanceDeadbandDeg);
        double Roll_deg = odometryControl.getRoll_deg();
        if (Math.abs(Roll_deg) > BalanceDeadband_deg) {
            double output = closedLoopsControl.calculateBalanceOutput(Roll_deg, 0);
            driveControl.drive(new ChassisSpeeds(output, 0.0, 0.0));
        } else {
            driveControl.stopSticky();

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
