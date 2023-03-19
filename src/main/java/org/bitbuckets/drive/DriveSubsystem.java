package org.bitbuckets.drive;

import com.pathplanner.lib.PathPlannerTrajectory;
import config.Drive;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj.DriverStation;
import org.bitbuckets.OperatorInput;
import org.bitbuckets.auto.AutoSubsystem;
import org.bitbuckets.drive.balance.BalanceControl;
import org.bitbuckets.drive.holo.HoloControl;
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

        //TODO DONT USE THIS
        if (input.isResetGyroPressed()) {
            odometryControl.zeroGyro(); //THIS ONLY WORKS IF YOU ARE FACING AN ALLIANCE WALL OTHERWISE BAD THINGS
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

            ChassisSpeeds targetSpeeds_trueField = holoControl.calculateTrueFieldControlEffort(
                    state.poseMeters,
                    state.velocityMetersPerSecond
            );

            ChassisSpeeds targetSpeeds_robotRelative = trueFieldRelativeToInitializationRelative(
                    targetSpeeds_trueField,
                    odometryControl.getGyro().getRotation2d_initializationRelative()
            );
            driveControl.drive(targetSpeeds_robotRelative);

        } else {
            driveControl.stop();
        }
    }


    public static ChassisSpeeds trueFieldRelativeToInitializationRelative(ChassisSpeeds robotSpeeds_trueFieldRelative, Rotation2d robotAngle_initializationRelative) {

        //true field relative's zero is in the direction from the blue wall to red wall
        //initialization relative's zero is either the direction facing the blue wall or the direction facing the red wall

        Rotation2d trueFieldRelative;
        //we have to switch this because the robot always considers facing the alliance wall as 0, which isn't true field relative
        //its only  true field relative when red.

        if (DriverStation.getAlliance() == DriverStation.Alliance.Blue) {
            //The initialization relative angle thinks zero is in the direction of the blue wall, so flip so that 0 is in the direction of the red wall
            trueFieldRelative = robotAngle_initializationRelative;
        } else {
            //the initialization relative angle thinks zero is in the direction of the red wall, so do nothing
            trueFieldRelative = robotAngle_initializationRelative;
        }


        double vx_initializationRelative =
                trueFieldRelative.getCos() * robotSpeeds_trueFieldRelative.vxMetersPerSecond
                        + trueFieldRelative.getSin() * robotSpeeds_trueFieldRelative.vxMetersPerSecond;

        double vy_initializationRelative =
                -trueFieldRelative.getCos() * robotSpeeds_trueFieldRelative.vyMetersPerSecond
                        + trueFieldRelative.getSin() * robotSpeeds_trueFieldRelative.vyMetersPerSecond;


        return new ChassisSpeeds(vx_initializationRelative, vy_initializationRelative, robotSpeeds_trueFieldRelative.omegaRadiansPerSecond);

    }

    public static ChassisSpeeds allianceRelativeToInitializationRelative(ChassisSpeeds robotSpeeds_allianceRelative, Rotation2d initializationRelative) {
        return ChassisSpeeds.fromFieldRelativeSpeeds(robotSpeeds_allianceRelative.vxMetersPerSecond, robotSpeeds_allianceRelative.vyMetersPerSecond, robotSpeeds_allianceRelative.omegaRadiansPerSecond, initializationRelative);
    }


    //NOT WORKING DONT USE
    void teleopVision() {

        var targetPose = visionControl.estimateBestVisionTarget();
        if (targetPose.isPresent()) {
            ChassisSpeeds controlEffort_trueFieldRelative = holoControl.calculateTrueFieldControlEffort(targetPose.get().toPose2d(), 1);

            driveControl.drive(
                    trueFieldRelativeToInitializationRelative(
                            controlEffort_trueFieldRelative,
                            odometryControl.getGyro().getRotation2d_initializationRelative()

                    )
            );
        }
    }

    void teleopNormal() {

        if (input.stopStickyPressed()) {
            driveControl.stop();
            return;
        }

        ChassisSpeeds desiredSpeeds_allianceRelative = new ChassisSpeeds(
                input.getDesiredX_fieldRelative(),
                input.getDesiredY_fieldRelative(),
                input.getDesiredRotation_initializationRelative()
        );


        debuggable.log("x-field", desiredSpeeds_allianceRelative.vxMetersPerSecond);
        debuggable.log("y-field", desiredSpeeds_allianceRelative.vyMetersPerSecond);


        switch (orientation.readValue()) {
            case FIELD_ORIENTED:

                ChassisSpeeds desiredSpeeds_robotRelative = allianceRelativeToInitializationRelative(
                        desiredSpeeds_allianceRelative,
                        odometryControl.getGyro().getRotation2d_initializationRelative()
                );

                debuggable.log("x-robot", desiredSpeeds_robotRelative.vxMetersPerSecond);
                debuggable.log("y-robot", desiredSpeeds_robotRelative.vyMetersPerSecond);

                driveControl.drive(
                        desiredSpeeds_robotRelative
                );

                break;
            case ROBOT_ORIENTED:

                driveControl.drive(
                        desiredSpeeds_allianceRelative //this is considering alliance relative speeds as initialization relative speeds, which is wrong.
                );

                break;
        }

    }

    boolean shouldStop = false;

    void balance() {
        double Pitch_deg = odometryControl.getGyro().getAllianceRelativePitch_deg();

        debuggable.log("pitch-now", Pitch_deg);
        debuggable.log("accel", odometryControl.getGyro().getAccelerationZ());
        if (Math.abs(Pitch_deg) > 2) {

            if (shouldStop) {
                driveControl.drive(new ChassisSpeeds(0, 0, 0));
                return;
            }

            if (odometryControl.getGyro().getAccelerationZ() > Drive.ACCEL_THRESHOLD_AUTOBALANCE) {
                shouldStop = true;

                //all initialization relative. Is t
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
