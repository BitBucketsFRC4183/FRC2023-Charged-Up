package org.bitbuckets.robot;

import edu.wpi.first.wpilibj.Joystick;
import org.bitbuckets.arm.ArmSubsystem;
import org.bitbuckets.auto.AutoControl;
import org.bitbuckets.auto.AutoControlSetup;
import org.bitbuckets.auto.AutoPath;
import org.bitbuckets.drive.DriveInput;
import org.bitbuckets.drive.DriveSDSConstants;
import org.bitbuckets.drive.DriveSDSSubsystem;
import org.bitbuckets.drive.balance.AutoAxisControl;
import org.bitbuckets.drive.balance.AutoAxisSetup;
import org.bitbuckets.drive.controlsds.DriveControlSDS;
import org.bitbuckets.drive.controlsds.neo.NeoControlSDSSetup;
import org.bitbuckets.drive.controlsds.neo.NeoDriveControllerSetup;
import org.bitbuckets.drive.controlsds.neo.NeoSteerControllerSetup;
import org.bitbuckets.drive.controlsds.neo.SwerveModuleSetup;
import org.bitbuckets.gyro.GyroControl;
import org.bitbuckets.gyro.GyroControlSetup;
import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.ProcessPath;
import org.bitbuckets.lib.hardware.PIDIndex;
import org.bitbuckets.lib.tune.IValueTuner;
import org.bitbuckets.lib.vendor.spark.SparkSetup;
import org.bitbuckets.lib.vendor.thrifty.ThriftyEncoderSetup;

public class RobotSetup implements ISetup<RobotContainer> {

    final RobotStateControl robotStateControl;

    public RobotSetup(RobotStateControl robotStateControl) {
        this.robotStateControl = robotStateControl;
    }

    @Override
    public RobotContainer build(ProcessPath path) {
        double[] predefPid = PIDIndex.CONSTANTS(1, 0, 0.1, 0, 0);

//
//        ModuleSetup frontLeftModule = new ModuleSetup(
//                new SparkSetup(
//                        1,
//                        true,
//                        DriveConstants.DRIVE_REDUCTION,
//                        DriveConstants.DRIVE_METERS_FACTOR,
//                        80,
//                        PIDIndex.EMPTY
//                ),
//                new SparkSetup(
//                        2,
//                        true,
//                        DriveConstants.TURN_REDUCTION,
//                        DriveConstants.TURN_METERS_FACTOR,
//                        20,
//                        predefPid
//                )
//        );
//
//        ModuleSetup frontRightModule = new ModuleSetup(
//                new SparkSetup(
//                        7,
//                        true,
//                        DriveConstants.DRIVE_REDUCTION,
//                        DriveConstants.DRIVE_METERS_FACTOR,
//                        80,
//                        PIDIndex.EMPTY
//                ),
//                new SparkSetup(
//                        8,
//                        true,
//                        DriveConstants.TURN_REDUCTION,
//                        DriveConstants.TURN_METERS_FACTOR,
//                        20,
//                        predefPid
//                )
//        );
//
//        ModuleSetup backLeftModule = new ModuleSetup(
//                new SparkSetup(
//                        5,
//                        true,
//                        DriveConstants.DRIVE_REDUCTION,
//                        DriveConstants.DRIVE_METERS_FACTOR,
//                        80,
//                        PIDIndex.EMPTY
//                ),
//                new SparkSetup(
//                        6,
//                        true,
//                        DriveConstants.TURN_REDUCTION,
//                        DriveConstants.TURN_METERS_FACTOR,
//                        20,
//                        predefPid
//                )
//        );
//
//        ModuleSetup backRightModule = new ModuleSetup(
//                new SparkSetup(
//                        3,
//                        true,
//                        DriveConstants.DRIVE_REDUCTION,
//                        DriveConstants.DRIVE_METERS_FACTOR,
//                        80,
//                        PIDIndex.EMPTY
//                ),
//                new SparkSetup(
//                        4,
//                        true,
//                        DriveConstants.TURN_REDUCTION,
//                        DriveConstants.TURN_METERS_FACTOR,
//                        20,
//                        predefPid
//                )
//        );
//
//        DriveControlSDS driveControl = new DriveControlSDSSetup(
//                frontLeftModule,
//                frontRightModule,
//                backLeftModule,
//                backRightModule
//        ).build(path.addChild("drive-control"));

        int frontLeftModuleDriveMotor_ID = 5;
        int frontLeftModuleSteerMotor_ID = 4;
        int frontLeftModuleSteerEncoder_CHANNEL = 1;

        int frontRightModuleDriveMotor_ID = 6;
        int frontRightModuleSteerMotor_ID = 7;
        int frontRightModuleSteerEncoder_CHANNEL = 2;

        int backLeftModuleDriveMotor_ID = 1;
        int backLeftModuleSteerMotor_ID = 2;
        int backLeftModuleSteerEncoder_CHANNEL = 3;

        int backRightModuleDriveMotor_ID = 8;
        int backRightModuleSteerMotor_ID = 10;
        int backRightModuleSteerEncoder_CHANNEL = 4;

        DriveControlSDS driveControl = new NeoControlSDSSetup(
                new SwerveModuleSetup(
                        new NeoDriveControllerSetup(new SparkSetup(frontLeftModuleDriveMotor_ID, PIDIndex.EMPTY), DriveSDSConstants.MK4I_L2),
                        new NeoSteerControllerSetup(
                                new SparkSetup(frontLeftModuleSteerMotor_ID, PIDIndex.EMPTY),
                                new ThriftyEncoderSetup(1),
                                DriveSDSConstants.MK4I_L2
                        )
                ),
                new SwerveModuleSetup(
                        new NeoDriveControllerSetup(new SparkSetup(frontRightModuleDriveMotor_ID, PIDIndex.EMPTY), DriveSDSConstants.MK4I_L2),
                        new NeoSteerControllerSetup(
                                new SparkSetup(frontRightModuleSteerMotor_ID, PIDIndex.EMPTY),
                                new ThriftyEncoderSetup(2),
                                DriveSDSConstants.MK4I_L2
                        )
                ),
                new SwerveModuleSetup(
                        new NeoDriveControllerSetup(new SparkSetup(backLeftModuleDriveMotor_ID, PIDIndex.EMPTY), DriveSDSConstants.MK4I_L2),
                        new NeoSteerControllerSetup(
                                new SparkSetup(backLeftModuleSteerMotor_ID, PIDIndex.EMPTY),
                                new ThriftyEncoderSetup(3),
                                DriveSDSConstants.MK4I_L2
                        )
                ),
                new SwerveModuleSetup(
                        new NeoDriveControllerSetup(new SparkSetup(backRightModuleDriveMotor_ID, PIDIndex.EMPTY), DriveSDSConstants.MK4I_L2),
                        new NeoSteerControllerSetup(
                                new SparkSetup(backRightModuleSteerMotor_ID, PIDIndex.EMPTY),
                                new ThriftyEncoderSetup(4),
                                DriveSDSConstants.MK4I_L2
                        )
                )
        ).build(path.addChild("drive-control"));

        DriveInput input = new DriveInput(new Joystick(0));

        AutoControl autoControl = new AutoControlSetup().build(path.addChild("auto-control"));
        GyroControl gyroControl = new GyroControlSetup(5).build(path.addChild("gyro-control"));
        AutoAxisControl autoAxisControl = new AutoAxisSetup().build(path.addChild("axis-control"));
        IValueTuner<AutoPath> pathTuneable = path.generateTuneable("path", AutoPath.TEST_PATH);


        DriveSDSSubsystem driveSubsystem = new DriveSDSSubsystem(input, robotStateControl, gyroControl, autoAxisControl, driveControl, autoControl, pathTuneable);

//        ArmInput armInput = new ArmInput(
//                new Joystick(1)
//        );


//        ArmControlSetup armControlSetup = new ArmControlSetup(
//                new TalonSetup(1, false, 0, 0, 0, new double[]{0, 0, 0}),
//                new TalonSetup(2, false, 0, 0, 0, new double[]{0, 0, 0})
//        );

//        ArmControl armControl = armControlSetup.build(path.addChild("arm-control"));

//        armInput = new ArmInput(new Joystick(1));

//        ArmSubsystem armSubsystem = new ArmSubsystem(armInput, armControl);
        ArmSubsystem armSubsystem = null;


        //SYSTEMS_GREEN.setOn(); //LET'S WIN SOME DAMN REGIONALS!!

        return new RobotContainer(driveSubsystem, armSubsystem);
    }
}
