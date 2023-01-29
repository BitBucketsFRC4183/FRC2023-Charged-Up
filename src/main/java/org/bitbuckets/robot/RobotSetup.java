package org.bitbuckets.robot;

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
import org.bitbuckets.lib.hardware.MotorIndex;
import org.bitbuckets.lib.hardware.IMotorController;
import org.bitbuckets.lib.hardware.MotorConfig;
import org.bitbuckets.lib.log.DataLogger;
import org.bitbuckets.lib.tune.IValueTuner;
import org.bitbuckets.lib.util.MockingUtil;
import org.bitbuckets.lib.vendor.spark.SparkSetup;
import org.bitbuckets.lib.vendor.thrifty.ThriftyEncoderSetup;
import org.bitbuckets.lib.vendor.spark.SparkSetup;
import org.bitbuckets.lib.vendor.thrifty.ThriftyEncoderSetup;

import java.util.Optional;

public class RobotSetup implements ISetup<RobotContainer> {

    final RobotStateControl robotStateControl;

    public RobotSetup(RobotStateControl robotStateControl) {
        this.robotStateControl = robotStateControl;
    }

    @Override
    public RobotContainer build(ProcessPath path) {

        MotorConfig config = new MotorConfig(1,1,1, false, false, 20, false, false, Optional.empty());

        DriveControlSDS driveControl = new NeoControlSDSSetup(
                new SwerveModuleSetup(
                        new NeoDriveControllerSetup(new SparkSetup(DriveSDSConstants.frontLeftModuleDriveMotor_ID, config), DriveSDSConstants.MK4I_L2),
                        new NeoSteerControllerSetup(
                                new SparkSetup(DriveSDSConstants.frontLeftModuleSteerMotor_ID, config),
                                new ThriftyEncoderSetup(1),
                                DriveSDSConstants.MK4I_L2
                        )
                ),
                new SwerveModuleSetup(
                        new NeoDriveControllerSetup(new SparkSetup(DriveSDSConstants.frontRightModuleDriveMotor_ID, config), DriveSDSConstants.MK4I_L2),
                        new NeoSteerControllerSetup(
                                new SparkSetup(DriveSDSConstants.frontRightModuleSteerMotor_ID, config),
                                new ThriftyEncoderSetup(2),
                                DriveSDSConstants.MK4I_L2
                        )
                ),
                new SwerveModuleSetup(
                        new NeoDriveControllerSetup(new SparkSetup(DriveSDSConstants.backLeftModuleDriveMotor_ID, config), DriveSDSConstants.MK4I_L2),
                        new NeoSteerControllerSetup(
                                new SparkSetup(DriveSDSConstants.backLeftModuleSteerMotor_ID, config),
                                new ThriftyEncoderSetup(3),
                                DriveSDSConstants.MK4I_L2
                        )
                ),
                new SwerveModuleSetup(
                        new NeoDriveControllerSetup(new SparkSetup(DriveSDSConstants.backRightModuleDriveMotor_ID, config), DriveSDSConstants.MK4I_L2),
                        new NeoSteerControllerSetup(
                                new SparkSetup(DriveSDSConstants.backRightModuleSteerMotor_ID, config),
                                new ThriftyEncoderSetup(4),
                                DriveSDSConstants.MK4I_L2
                        )
                )
        ).build(path.addChild("drive-control"));

        DriveInput input = new DriveInput(new Joystick(0));
        AutoControl autoControl = new AutoControlSetup().build(path.addChild("auto-control"));
        GyroControl gyroControl = new GyroControlSetup(5).build(path.addChild("gyro-control"));
        AutoAxisControl autoAxisControl = new AutoAxisSetup().build(path.addChild("axis-control"));
        IValueTuner<AutoPath> pathTuneable = path.generateValueTuner("path", AutoPath.TEST_PATH);


        DriveSDSSubsystem driveSubsystem = new DriveSDSSubsystem(input, robotStateControl, gyroControl, autoAxisControl, driveControl, autoControl, pathTuneable);

        //ArmControl armControl = armControlSetup.build(path.addChild("arm-control"));

        //armInput = new ArmInput(new Joystick(1));
        //labels: high priority
        //TODO use neos here
        ArmControlSetup armControlSetup = new ArmControlSetup(
                MockingUtil.noops(IMotorController.class),
                MockingUtil.noops(IMotorController.class)
        );

        //ArmSubsystem armSubsystem = new ArmSubsystem(armInput, armControl);
        ArmControl armControl = armControlSetup.build(path.addChild("arm-control"));
        ArmInput armInput = new ArmInput(new Joystick(1));
        ArmSubsystem armSubsystem = new ArmSubsystem(armInput, armControl);
        armSubsystem = null;


        //SYSTEMS_GREEN.setOn(); //LET'S WIN SOME DAMN REGIONALS!!

        return new RobotContainer(driveSubsystem);
    }
}
