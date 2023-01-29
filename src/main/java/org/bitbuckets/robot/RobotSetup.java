package org.bitbuckets.robot;

import com.ctre.phoenix.sensors.WPI_PigeonIMU;
import com.revrobotics.REVPhysicsSim;
import edu.wpi.first.wpilibj.Joystick;
import org.bitbuckets.arm.ArmControl;
import org.bitbuckets.arm.ArmControlSetup;
import org.bitbuckets.arm.ArmInput;
import org.bitbuckets.arm.ArmSubsystem;
import org.bitbuckets.auto.AutoControl;
import org.bitbuckets.auto.AutoControlSetup;
import org.bitbuckets.auto.AutoPath;
import org.bitbuckets.drive.DriveInput;
import org.bitbuckets.drive.DriveSDSConstants;
import org.bitbuckets.drive.DriveSubsystem;
import org.bitbuckets.drive.balance.AutoAxisControl;
import org.bitbuckets.drive.balance.AutoAxisSetup;
import org.bitbuckets.drive.controlsds.DriveControl;
import org.bitbuckets.drive.controlsds.sds.DriveControlSetup;
import org.bitbuckets.drive.controlsds.sds.DriveControllerSetup;
import org.bitbuckets.drive.controlsds.sds.SteerControllerSetup;
import org.bitbuckets.drive.controlsds.sds.SwerveModuleSetup;
import org.bitbuckets.gyro.GyroControl;
import org.bitbuckets.gyro.GyroControlSetup;
import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.ProcessPath;
import org.bitbuckets.lib.hardware.IMotorController;
import org.bitbuckets.lib.hardware.MotorConfig;
import org.bitbuckets.lib.tune.IValueTuner;
import org.bitbuckets.lib.util.MockingUtil;
import org.bitbuckets.lib.vendor.ctre.CANCoderAbsoluteEncoderSetup;
import org.bitbuckets.lib.vendor.ctre.TalonDriveMotorSetup;
import org.bitbuckets.lib.vendor.ctre.TalonSteerMotorSetup;
import org.bitbuckets.lib.vendor.spark.SparkDriveMotorSetup;
import org.bitbuckets.lib.vendor.spark.SparkSteerMotorSetup;
import org.bitbuckets.lib.vendor.thrifty.ThriftyEncoderSetup;

public class RobotSetup implements ISetup<RobotContainer> {

    final RobotStateControl robotStateControl;

    public RobotSetup(RobotStateControl robotStateControl) {
        this.robotStateControl = robotStateControl;
    }

    @Override
    public RobotContainer build(ProcessPath path) {
//        DriveControl driveControl = buildNeoDriveControl(path);
        DriveControl driveControl = buildTalonDriveControl(path);

        DriveInput input = new DriveInput(new Joystick(0));
        AutoControl autoControl = new AutoControlSetup().build(path.addChild("auto-control"));
        GyroControl gyroControl = new GyroControlSetup(5).build(path.addChild("gyro-control"));
        AutoAxisControl autoAxisControl = new AutoAxisSetup().build(path.addChild("axis-control"));
        IValueTuner<AutoPath> pathTuneable = path.generateValueTuner("path", AutoPath.TEST_PATH);


        DriveSubsystem driveSubsystem = new DriveSubsystem(input, robotStateControl, gyroControl, autoAxisControl, driveControl, autoControl, pathTuneable);

        //labels: high priority
        //TODO use neos here
        ArmControlSetup armControlSetup = new ArmControlSetup(
                MockingUtil.noops(IMotorController.class),
                MockingUtil.noops(IMotorController.class)
        );

        ArmControl armControl = armControlSetup.build(path.addChild("arm-control"));
        ArmInput armInput = new ArmInput(new Joystick(1));
        ArmSubsystem armSubsystem = new ArmSubsystem(armInput, armControl);
        armSubsystem = null;


        //SYSTEMS_GREEN.setOn(); //LET'S WIN SOME DAMN REGIONALS!!

        return new RobotContainer(driveSubsystem, armSubsystem);
    }

    private static DriveControl buildNeoDriveControl(ProcessPath path) {
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

        DriveControl driveControl = new DriveControlSetup(
                new SwerveModuleSetup(
                        new DriveControllerSetup(new SparkDriveMotorSetup(frontLeftModuleDriveMotor_ID, MotorConfig.Empty, DriveSDSConstants.MK4I_L2)),
                        new SteerControllerSetup(
                                new SparkSteerMotorSetup(frontLeftModuleSteerMotor_ID, MotorConfig.Empty, DriveSDSConstants.MK4I_L2),
                                new ThriftyEncoderSetup(frontLeftModuleSteerEncoder_CHANNEL))
                ),
                new SwerveModuleSetup(
                        new DriveControllerSetup(new SparkDriveMotorSetup(frontRightModuleDriveMotor_ID, MotorConfig.Empty, DriveSDSConstants.MK4I_L2)),
                        new SteerControllerSetup(
                                new SparkSteerMotorSetup(frontRightModuleSteerMotor_ID, MotorConfig.Empty, DriveSDSConstants.MK4I_L2),
                                new ThriftyEncoderSetup(frontRightModuleSteerEncoder_CHANNEL))
                ),
                new SwerveModuleSetup(
                        new DriveControllerSetup(new SparkDriveMotorSetup(backLeftModuleDriveMotor_ID, MotorConfig.Empty, DriveSDSConstants.MK4I_L2)),
                        new SteerControllerSetup(
                                new SparkSteerMotorSetup(backLeftModuleSteerMotor_ID, MotorConfig.Empty, DriveSDSConstants.MK4I_L2),
                                new ThriftyEncoderSetup(backLeftModuleSteerEncoder_CHANNEL))
                ),
                new SwerveModuleSetup(
                        new DriveControllerSetup(new SparkDriveMotorSetup(backRightModuleDriveMotor_ID, MotorConfig.Empty, DriveSDSConstants.MK4I_L2)),
                        new SteerControllerSetup(
                                new SparkSteerMotorSetup(backRightModuleSteerMotor_ID, MotorConfig.Empty, DriveSDSConstants.MK4I_L2),
                                new ThriftyEncoderSetup(backRightModuleSteerEncoder_CHANNEL))
                ),
                new WPI_PigeonIMU(5)
        ).build(path.addChild("drive-control"));

        REVPhysicsSim.getInstance().run();
        return driveControl;
    }

    private static DriveControl buildTalonDriveControl(ProcessPath path) {

        int frontLeftModuleDriveMotor_ID = 1;
        int frontLeftModuleSteerMotor_ID = 2;
        int frontLeftModuleSteerEncoder_ID = 9;

        int frontRightModuleDriveMotor_ID = 7;
        int frontRightModuleSteerMotor_ID = 8;
        int frontRightModuleSteerEncoder_ID = 12;

        int backLeftModuleDriveMotor_ID = 5;
        int backLeftModuleSteerMotor_ID = 6;
        int backLeftModuleSteerEncoder_ID = 11;

        int backRightModuleDriveMotor_ID = 3;
        int backRightModuleSteerMotor_ID = 4;
        int backRightModuleSteerEncoder_ID = 10;

        DriveControl driveControl = new DriveControlSetup(
                new SwerveModuleSetup(
                        new DriveControllerSetup(new TalonDriveMotorSetup(frontLeftModuleDriveMotor_ID, DriveSDSConstants.MK4_L2)),
                        new SteerControllerSetup(
                                new TalonSteerMotorSetup(frontLeftModuleSteerMotor_ID, DriveSDSConstants.MK4_L2),
                                new CANCoderAbsoluteEncoderSetup(frontLeftModuleSteerEncoder_ID))
                ),
                new SwerveModuleSetup(
                        new DriveControllerSetup(new TalonDriveMotorSetup(frontRightModuleDriveMotor_ID, DriveSDSConstants.MK4_L2)),
                        new SteerControllerSetup(
                                new TalonSteerMotorSetup(frontRightModuleSteerMotor_ID, DriveSDSConstants.MK4_L2),
                                new CANCoderAbsoluteEncoderSetup(frontRightModuleSteerEncoder_ID))
                ),
                new SwerveModuleSetup(
                        new DriveControllerSetup(new TalonDriveMotorSetup(backLeftModuleDriveMotor_ID, DriveSDSConstants.MK4_L2)),
                        new SteerControllerSetup(
                                new TalonSteerMotorSetup(backLeftModuleSteerMotor_ID, DriveSDSConstants.MK4_L2),
                                new CANCoderAbsoluteEncoderSetup(backLeftModuleSteerEncoder_ID))
                ),
                new SwerveModuleSetup(
                        new DriveControllerSetup(new TalonDriveMotorSetup(backRightModuleDriveMotor_ID, DriveSDSConstants.MK4_L2)),
                        new SteerControllerSetup(
                                new TalonSteerMotorSetup(backRightModuleSteerMotor_ID, DriveSDSConstants.MK4_L2),
                                new CANCoderAbsoluteEncoderSetup(backRightModuleSteerEncoder_ID))
                ),
                new WPI_PigeonIMU(5)
        ).build(path.addChild("drive-control"));

        return driveControl;
    }

}
