package org.bitbuckets;

import com.ctre.phoenix.sensors.Pigeon2;
import config.*;
import edu.wpi.first.math.controller.HolonomicDriveController;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj.IterativeRobotBase;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import org.bitbuckets.arm.ArmControl;
import org.bitbuckets.arm.commands.GripperIntakeCommand;
import org.bitbuckets.auto.*;
import org.bitbuckets.bootstrap.Robot;
import org.bitbuckets.arm.commands.ArmMoveToPositionCommand;
import org.bitbuckets.arm.commands.StopGripperCommand;
import org.bitbuckets.drive.DriveControl;
import org.bitbuckets.drive.IDriveControl;
import org.bitbuckets.drive.DriveControlSetup;
import org.bitbuckets.drive.commands.*;
import org.bitbuckets.lib.*;
import org.bitbuckets.lib.control.IPIDCalculator;
import org.bitbuckets.lib.control.PIDCalculatorSetup;
import org.bitbuckets.lib.core.HasLoop;
import org.bitbuckets.lib.tune.IValueTuner;
import org.bitbuckets.lib.util.LateSupplier;
import org.bitbuckets.lib.util.MockingUtil;
import org.bitbuckets.lib.vendor.ctre.PigeonGyroSetup;
import org.bitbuckets.odometry.IOdometryControl;
import org.bitbuckets.odometry.OdometryControlSetup;
import org.bitbuckets.odometry.SimOdometryControlSetup;
import org.bitbuckets.vision.IVisionControl;
import org.bitbuckets.vision.VisionControlSetup;

public class RobotSetup implements ISetup<Void> {

    final IterativeRobotBase robot;

    public RobotSetup(IterativeRobotBase robot) {
        this.robot = robot;
    }


    @Override
    public Void build(IProcess self) {
        self.registerLogicLoop((HasLoop) () -> CommandScheduler.getInstance().run());

        SwerveDriveKinematics KINEMATICS = DriveTurdSpecific.KINEMATICS; //TODO make this swappable
        OperatorInput operatorInput = new OperatorInput(
                new Joystick(1),
                new Joystick(0)
        );


        //if only these could be children of the drive subsystem... TODO fix this in mattlib future editions



        IDriveControl driveControl = self.childSetup(
                "drive-ctrl",
                new ToggleableSetup<>(
                        Enabled.drive,
                        IDriveControl.class,
                        new DriveControlSetup(
                                KINEMATICS,
                                DriveSetups.FRONT_LEFT,
                                DriveSetups.FRONT_RIGHT,
                                DriveSetups.BACK_LEFT,
                                DriveSetups.BACK_RIGHT
                        )
                )

        );

        LateSupplier<Pose3d> robotPose = new LateSupplier<>();
        IVisionControl visionControl = self.childSetup(
                "vision-ctrl",
                new ToggleableSetup<>(
                        Enabled.vision,
                        IVisionControl.class,
                        new VisionControlSetup(
                                robotPose,
                                Vision.CAMERA_NAME,
                                Vision.LAYOUTCONTAINER.LAYOUT,
                                Vision.CONFIG,
                                Vision.STRATEGY,
                                operatorInput
                        )
                )
        );
        IOdometryControl odometryControl = self.childSetup(
                "odometry-control",
                new ToggleableSetup<>(
                        Enabled.drive,
                        IOdometryControl.class,
                        /*new PidgeonOdometryControlSetup( //needs to be swappable
                                driveControl,
                                visionControl,
                                KINEMATICS,
                                MotorIds.PIDGEON_IMU_ID
                        )*/
                        new SwapSetup<>(
                                MockingUtil.noops(IOdometryControl.class),
                                new OdometryControlSetup(
                                        Drive.STD_VISION,
                                        KINEMATICS,
                                        driveControl,
                                        visionControl,
                                        new PigeonGyroSetup(
                                                MotorIds.PIGEON_IMU_ID,
                                                Pigeon2.AxisDirection.PositiveY,
                                                Pigeon2.AxisDirection.PositiveZ
                                        )
                                ),
                                new SimOdometryControlSetup(
                                        KINEMATICS,
                                        driveControl
                                )
                        )

                )
        );
        robotPose.set(() -> new Pose3d(odometryControl.estimateFusedPose2d()));
        //robotPose.set(() -> new Pose3d(new Pose2d(10, 0, Rotation2d.fromDegrees(0))));

        IAutoControl autoControl = self.childSetup(
                "auto-control",
                new AutoControlSetup(odometryControl)
        );


        AutoSubsystem autoSubsystem = self.childSetup(
                "auto-system",
                new ToggleableSetup<>(
                        Enabled.auto,
                        AutoSubsystem.class,
                        new AutoSubsystemSetup(
                            a -> autoControl
                        )
                )
        );

        ArmControl arm = self.childSetup("arm-control", ArmSetups.ARM_CONTROL);


        HolonomicDriveController visionHoloController = new HolonomicDriveController(
                new PIDController(Drive.X_HOLO_PID.kP, Drive.X_HOLO_PID.kI, Drive.X_HOLO_PID.kD),
                new PIDController(Drive.Y_HOLO_PID.kP, Drive.Y_HOLO_PID.kI, Drive.Y_HOLO_PID.kD),
                new ProfiledPIDController(
                        Drive.Y_HOLO_PID.kP,
                        Drive.Y_HOLO_PID.kI,
                        Drive.Y_HOLO_PID.kD,
                        Drive.THETA_CONSTRAINTS
                )
        );
        HolonomicDriveController autoHoloController = new HolonomicDriveController(
                new PIDController(Drive.X_HOLO_PID.kP, Drive.X_HOLO_PID.kI, Drive.X_HOLO_PID.kD),
                new PIDController(Drive.Y_HOLO_PID.kP, Drive.Y_HOLO_PID.kI, Drive.Y_HOLO_PID.kD),
                new ProfiledPIDController(
                        Drive.Y_HOLO_PID.kP,
                        Drive.Y_HOLO_PID.kI,
                        Drive.Y_HOLO_PID.kD,
                        Drive.THETA_CONSTRAINTS
                )
        );

        IPIDCalculator balancePidController = self.childSetup("balance-pid",new PIDCalculatorSetup(Drive.DRIVE_BALANCE_PID));
        IPIDCalculator responseTimeController = self.childSetup("responseTime-pid", new PIDCalculatorSetup(Drive.TIME_RESPONSE));
        IValueTuner<AutoPath> autoPath = self.generateTuner(ITuneAs.ENUM(AutoPath.class), "auto-path", Auto.DEFAULT_NO_WIFI);



        //                 command based
        
        float errorTolerance = ArmControl.COMPONENT.errorTolerance_rotations();

        



        
        
        //commands
            
        Command balanceDrive = new BalanceCommand(
                odometryControl,
                driveControl,
                self.getDebuggable(),
                balancePidController,
                responseTimeController
        );

        //arm related
        Command storeArm = new ArmMoveToPositionCommand(arm, errorTolerance, ArmControl.COMPONENT.storePosition());
        Command humanIntake = new ArmMoveToPositionCommand(arm, errorTolerance, ArmControl.COMPONENT.humanIntakePosition());
        Command groundIntake = new ArmMoveToPositionCommand(arm, errorTolerance, ArmControl.COMPONENT.groundIntakePosition());
        Command unstow = new ArmMoveToPositionCommand(arm, errorTolerance, ArmControl.COMPONENT.unstowPosition());
        Command scoreHigh = new ArmMoveToPositionCommand(arm, errorTolerance, ArmControl.COMPONENT.scoreHighPosition());
        Command scoreMid = new ArmMoveToPositionCommand(arm, errorTolerance, ArmControl.COMPONENT.scoreMidPosition());
        Command stopArm = new InstantCommand(arm::doNothing,arm);

        //gripper related
        Command gripperIntake = new GripperIntakeCommand(arm, operatorInput);
        Command stopGripper = new StopGripperCommand(arm);

        //all defaults
        driveControl.setDefaultCommand(new InfTeleopCommand(operatorInput, driveControl, odometryControl, self.getDebuggable()));
        arm.setDefaultCommand(stopGripper.alongWith(stopArm));

        
        //inputs
        new Trigger(operatorInput::isResetGyroPressed).onTrue(Commands.run(odometryControl::zeroOdo));
        new Trigger(operatorInput::isAutoBalancePressed).whileTrue(balanceDrive);
        new Trigger(operatorInput::isVisionDrivePressed).whileTrue(
                new VisionDriveCommand(
                        visionHoloController,
                        visionControl,
                        odometryControl,
                        driveControl,
                        Drive.ACCEL_THRESHOLD_AUTOBALANCE
                )
        );
        
        new Trigger(operatorInput::isZeroArmPressed).onTrue(Commands.run(arm::zero));
        new Trigger(operatorInput::isStoragePressed).whileTrue(storeArm);
        new Trigger(operatorInput::isHumanIntakePressed).whileTrue(humanIntake);
        new Trigger(operatorInput::isScoreHighPressed).whileTrue(scoreHigh);
        new Trigger(operatorInput::isScoreMidPressed).whileTrue(scoreMid);
        new Trigger(() -> {
            return
                    Math.abs(operatorInput.getLowerArm_PercentOutput()) > ArmControl.COMPONENT.manualModeThresholdToGoToManual() ||
                    Math.abs(operatorInput.getUpperArm_PercentOutput()) > ArmControl.COMPONENT.manualModeThresholdToGoToManual();
        });


        new Trigger(operatorInput::openGripper).whileTrue(Commands.run(arm::openGripper));
        new Trigger(operatorInput::intakeGripper).whileTrue(gripperIntake);
        new Trigger(operatorInput::holdGripper).whileTrue(Commands.run(arm::gripperHold));



        // auto

        new Trigger(() -> autoSubsystem.sampleHasEventStarted("arm-stow")).onTrue(storeArm.andThen(stopGripper));
        new Trigger(() -> autoSubsystem.sampleHasEventStarted("arm-human-intake")).onTrue(humanIntake);
        new Trigger(() -> autoSubsystem.sampleHasEventStarted("arm-ground-intake")).onTrue(groundIntake);
        new Trigger(() -> autoSubsystem.sampleHasEventStarted("arm-stop")).onTrue(stopArm);
        new Trigger(() -> autoSubsystem.sampleHasEventStarted("arm-scoreHigh")).onTrue(scoreHigh);
        new Trigger(() -> autoSubsystem.sampleHasEventStarted("arm-unstow")).onTrue(unstow);
        new Trigger(robot::isAutonomousEnabled).whileTrue(
                new AutoDriveCommand(
                        autoHoloController,
                        autoSubsystem,
                        odometryControl,
                        driveControl
                )
        );
        new Trigger(() -> autoSubsystem.sampleHasEventStarted("auto-balance")).whileTrue(balanceDrive);
        
        
        new Trigger(autoSubsystem::isPathDone)
                .onTrue(new StopDriveCommand(driveControl))
                .onTrue(new StopGripperCommand(arm))
                .onTrue(stopArm);






        if (System.getenv().containsKey("CI")) {
            self.registerLogicLoop(new SimulatorKiller()); //kills robot after a bit if running on github, makes sure this robot runs
        }

        //set spawn location
        if (Robot.isSimulation()) {
            Translation2d number1 = StagingLocations.translations[0];
            odometryControl.setPos(new Pose2d(number1.getX(), number1.getY(), new Rotation2d()), new Rotation2d());
        }

            return null;
    }

    public static final class StagingLocations {
        public static final double centerOffsetX = Units.inchesToMeters(47.36);
        public static final double positionX = Units.inchesToMeters(651.25) / 2.0 - Units.inchesToMeters(47.36);
        public static final double firstY = Units.inchesToMeters(36.19);
        public static final double separationY = Units.inchesToMeters(48.0);
        public static final Translation2d[] translations = new Translation2d[4];

        static {
            for (int i = 0; i < translations.length; i++) {
                translations[i] = new Translation2d(positionX, firstY + (i * separationY));
            }
        }
    }


}
