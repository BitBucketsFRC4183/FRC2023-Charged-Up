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
import org.bitbuckets.drive.controlsds.DriveControlSDSDataAutoGen;
import org.bitbuckets.drive.controlsds.neo.*;
import org.bitbuckets.gyro.GyroControl;
import org.bitbuckets.gyro.GyroControlSetup;
import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.ProcessPath;
import org.bitbuckets.lib.hardware.MotorIndex;
import org.bitbuckets.lib.hardware.PIDIndex;
import org.bitbuckets.lib.log.DataLogger;
import org.bitbuckets.lib.tune.IValueTuner;

import org.bitbuckets.lib.vendor.ctre.TalonSetup;
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

        DriveControlSDS driveControl = new NeoControlSDSSetup(
                new SwerveModuleSetup(
                        new NeoDriveControllerSetup(new SparkSetup(DriveSDSConstants.frontLeftModuleDriveMotor_ID, MotorIndex.CONSTANTS(1,1,1, false, 20, false)), DriveSDSConstants.MK4I_L2),
                        new NeoSteerControllerSetup(
                                new SparkSetup(DriveSDSConstants.frontLeftModuleSteerMotor_ID, MotorIndex.CONSTANTS(1,1,1, false, 20, false)),
                                new ThriftyEncoderSetup(1),
                                DriveSDSConstants.MK4I_L2
                        )
                ),
                new SwerveModuleSetup(
                        new NeoDriveControllerSetup(new SparkSetup(DriveSDSConstants.frontRightModuleDriveMotor_ID, MotorIndex.CONSTANTS(1,1,1, false, 20, false)), DriveSDSConstants.MK4I_L2),
                        new NeoSteerControllerSetup(
                                new SparkSetup(DriveSDSConstants.frontRightModuleSteerMotor_ID, MotorIndex.CONSTANTS(1,1,1, false, 20, false)),
                                new ThriftyEncoderSetup(2),
                                DriveSDSConstants.MK4I_L2
                        )
                ),
                new SwerveModuleSetup(
                        new NeoDriveControllerSetup(new SparkSetup(DriveSDSConstants.backLeftModuleDriveMotor_ID, MotorIndex.CONSTANTS(1,1,1, false, 20, false)), DriveSDSConstants.MK4I_L2),
                        new NeoSteerControllerSetup(
                                new SparkSetup(DriveSDSConstants.backLeftModuleSteerMotor_ID, MotorIndex.CONSTANTS(1,1,1, false, 20, false)),
                                new ThriftyEncoderSetup(3),
                                DriveSDSConstants.MK4I_L2
                        )
                ),
                new SwerveModuleSetup(
                        new NeoDriveControllerSetup(new SparkSetup(DriveSDSConstants.backRightModuleDriveMotor_ID, MotorIndex.CONSTANTS(1,1,1, false, 20, false)), DriveSDSConstants.MK4I_L2),
                        new NeoSteerControllerSetup(
                                new SparkSetup(DriveSDSConstants.backRightModuleSteerMotor_ID, MotorIndex.CONSTANTS(1,1,1, false, 20, false)),
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

        //ArmSubsystem armSubsystem = new ArmSubsystem(armInput, armControl);

        //SYSTEMS_GREEN.setOn(); //LET'S WIN SOME DAMN REGIONALS!!

        return new RobotContainer(driveSubsystem);
    }
}
