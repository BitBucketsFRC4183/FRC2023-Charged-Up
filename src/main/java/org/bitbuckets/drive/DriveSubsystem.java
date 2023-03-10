package org.bitbuckets.drive;

import com.pathplanner.lib.PathPlannerTrajectory;
import edu.wpi.first.math.geometry.Pose3d;
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

import java.util.ArrayList;
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


    @Override
    public void loop() {
        if (input.isResetGyroPressed()) {
            odometryControl.zero();
        }

        handleStateTransitions();
        debuggable.log("state", nextStateShould.toString());
        handleLogic();


    }

    DriveFSM nextStateShould = DriveFSM.IDLE;

    void handleStateTransitions() {

        //handle forced overrides from the auto subsystem


        if (autoSubsystem.hasChanged() && autoSubsystem.state() == AutoFSM.DISABLED) {
            nextStateShould = DriveFSM.IDLE;
            return;
        }

        if (autoSubsystem.hasChanged() && autoSubsystem.state() == AutoFSM.AUTO_RUN) {
            nextStateShould = DriveFSM.AUTO_PATHFINDING;
            return;
        }

        if (autoSubsystem.hasChanged() && autoSubsystem.state() == AutoFSM.AUTO_ENDED) {
            driveControl.stop();
            nextStateShould = DriveFSM.IDLE; //Stop moving after the path says we are done
            return;
        }

        if (autoSubsystem.hasChanged() && autoSubsystem.state() == AutoFSM.TELEOP) {
            nextStateShould = DriveFSM.MANUAL;
            return;
        }

        //handle event overrides from the auto subsystem

        if (autoSubsystem.state() == AutoFSM.AUTO_RUN) {
            if (autoSubsystem.sampleHasEventStarted("autoBalance")) {
                nextStateShould = DriveFSM.BALANCE;
                return;
            }

//
            if (autoSubsystem.sampleHasEventStarted("do-vision")) {
                nextStateShould = DriveFSM.VISION;
                return;
            }
        }



        //handle inputs from user

        if (autoSubsystem.state() == AutoFSM.TELEOP) {
            if (input.isVisionDrivePressed() && visionControl.estimateBestVisionTarget().isPresent()) {
                nextStateShould = DriveFSM.VISION;
                return;
            }

            if (nextStateShould == DriveFSM.VISION && !input.isVisionDrivePressed()) {
                nextStateShould = DriveFSM.MANUAL;
            }

            if (input.isAutoBalancePressed()) {
                nextStateShould = DriveFSM.BALANCE;
            }

            if (input.isManualDrivePressed()) {
                nextStateShould = DriveFSM.MANUAL;
            }
        }
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
            driveControl.stopSticky();
        }

        //if idle it will do nothing..
    }


    void autoPathFinding() {
        Optional<PathPlannerTrajectory.PathPlannerState> opt = autoSubsystem.samplePathPlannerState();
        if (opt.isPresent()) {
            ChassisSpeeds targetSpeeds = holoControl.calculatePose2DFromState(opt.get());
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
            ChassisSpeeds speeds = holoControl.calculatePose2D(targetPose.get().toPose2d(), 1, targetPose.get().toPose2d().getRotation());

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
            driveControl.stopSticky();
        }

        debuggable.log("x-output", xOutput);
        debuggable.log("y-output", yOutput);
        debuggable.log("rot-output", rotationOutput);

        switch (orientation.readValue()) {
            case FIELD_ORIENTED:
                if (xOutput == 0 && yOutput == 0 && rotationOutput == 0) {
                    driveControl.stopSticky();
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
                    driveControl.stopSticky();
                } else {
                    ChassisSpeeds robotOrient = new ChassisSpeeds(xOutput, yOutput, rotationOutput);
                    driveControl.drive(robotOrient);
                }
                break;
        }

    }

    void balance() {

        //TODO deadband only

        //This is bad and should be shifted somewhere else

        double Pitch_deg = odometryControl.getPitch_deg();

        debuggable.log("pitch-now", Pitch_deg);
        if (Math.abs(Pitch_deg) > 0.1) {
            double output = balanceControl.calculateBalanceOutput(Pitch_deg, 0);
            debuggable.log("control-output-autobalance", output);

            driveControl.drive(new ChassisSpeeds(output / 2.0, 0.0, 0.0));
        } else {
            debuggable.log("is-running-ab-2", false);
            driveControl.stop();

        }
    }


}
