package org.bitbuckets;

import com.ctre.phoenix.sensors.Pigeon2;
import config.*;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.wpilibj.Joystick;
import org.bitbuckets.arm.ArmSubsystem;
import org.bitbuckets.arm.ArmSubsystemSetup;
import org.bitbuckets.auto.AutoControlSetup;
import org.bitbuckets.auto.AutoSubsystem;
import org.bitbuckets.auto.AutoSubsystemSetup;
import org.bitbuckets.cubeCone.GamePiece;
import org.bitbuckets.cubeCone.GamePieceSetup;
import org.bitbuckets.drive.DriveSubsystem;
import org.bitbuckets.drive.DriveSubsystemSetup;
import org.bitbuckets.drive.IDriveControl;
import org.bitbuckets.drive.controlsds.DriveControlSetup;
import org.bitbuckets.drive.holo.HoloControlSetup;
import org.bitbuckets.lib.IProcess;
import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.SimulatorKiller;
import org.bitbuckets.lib.ToggleableSetup;
import org.bitbuckets.lib.util.LateSupplier;
import org.bitbuckets.lib.vendor.ctre.PidgeonGyroSetup;
import org.bitbuckets.odometry.IOdometryControl;
import org.bitbuckets.odometry.OdometryControlSetup;
import org.bitbuckets.vision.IVisionControl;
import org.bitbuckets.vision.VisionControlSetup;

public class RobotSetup implements ISetup<Void> {




    @Override
    public Void build(IProcess self) {



        SwerveDriveKinematics KINEMATICS = DriveTurdSpecific.KINEMATICS; //TODO make this swappable


        OperatorInput operatorInput = new OperatorInput(
                new Joystick(1),
                new Joystick(0)
        );

        GamePiece piece = self.childSetup("gp", new GamePieceSetup(operatorInput));
        self.childSetup("cone-cube", new GamePieceSetup(operatorInput));

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
                              Vision.STRATEGY
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
                        new OdometryControlSetup(
                                Drive.STD_VISION,
                                KINEMATICS,
                                driveControl,
                                visionControl,
                                new PidgeonGyroSetup(
                                        MotorIds.PIDGEON_IMU_ID,
                                        Pigeon2.AxisDirection.PositiveY,
                                        Pigeon2.AxisDirection.PositiveZ
                                )
                        )
                )
        );
        robotPose.set(() -> new Pose3d(odometryControl.estimateFusedPose2d()));

        AutoSubsystem autoSubsystem = self.childSetup(
                "auto-system",
                new ToggleableSetup<>(
                        Enabled.auto,
                        AutoSubsystem.class,
                        new AutoSubsystemSetup(
                            new AutoControlSetup(odometryControl)
                        )
                )
        );

        self.childSetup(
                "arm-system",
                new ToggleableSetup<>(
                        Enabled.arm,
                        ArmSubsystem.class,
                        new ArmSubsystemSetup(
                                operatorInput,
                                autoSubsystem,
                                ArmSetups.ARM_CONTROL,
                                piece
                        )
                )

        );

        self.childSetup(
                "drive-system",
                new ToggleableSetup<>(
                        Enabled.drive,
                        DriveSubsystem.class,
                        new DriveSubsystemSetup(
                                operatorInput,
                                autoSubsystem,
                                visionControl,
                                odometryControl,
                                DriveSetups.BALANCE_SETUP,
                                new HoloControlSetup(
                                        driveControl,
                                        odometryControl
                                ),
                                driveControl
                        )
                )
        );

        /**
         * Register the crasher runnable if we're in github
         */
        if (System.getenv().containsKey("CI")) {
            self.registerLogicLoop(new SimulatorKiller());
        }


        return null;
    }


}
