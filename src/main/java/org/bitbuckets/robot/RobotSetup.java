package org.bitbuckets.robot;

import com.ctre.phoenix.sensors.WPI_PigeonIMU;
import com.revrobotics.REVPhysicsSim;
import edu.wpi.first.wpilibj.Joystick;
import org.bitbuckets.arm.*;
import org.bitbuckets.auto.AutoControl;
import org.bitbuckets.auto.AutoControlSetup;
import org.bitbuckets.auto.AutoPath;
import org.bitbuckets.drive.DriveConstants;
import org.bitbuckets.drive.DriveInput;
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
import org.bitbuckets.lib.vendor.spark.SparkSetup;
import org.bitbuckets.lib.vendor.spark.SparkSteerMotorSetup;
import org.bitbuckets.lib.vendor.thrifty.ThriftyEncoderSetup;

import java.util.Optional;

public class RobotSetup implements ISetup<RobotContainer> {

    final RobotStateControl robotStateControl;

    public RobotSetup(RobotStateControl robotStateControl) {
        this.robotStateControl = robotStateControl;
    }

    @Override
    public RobotContainer build(ProcessPath path) {
        DriveControl driveControl = buildNeoDriveControl(path);
//        DriveControl driveControl = buildTalonDriveControl(path);


        DriveInput input = new DriveInput(new Joystick(0));
        AutoControl autoControl = new AutoControlSetup().build(path.addChild("auto-control"));
        GyroControl gyroControl = new GyroControlSetup(5).build(path.addChild("gyro-control"));
        AutoAxisControl autoAxisControl = new AutoAxisSetup().build(path.addChild("axis-control"));
        IValueTuner<AutoPath> pathTuneable = path.generateValueTuner("path", AutoPath.TEST_PATH);


        DriveSubsystem driveSubsystem = new DriveSubsystem(input, robotStateControl, gyroControl, autoAxisControl, driveControl, autoControl, pathTuneable);

        //labels: high priority
        //TODO use neos here
        ArmControlSetup armControlSetup = new ArmControlSetup(
                new SparkSetup(9, ArmConstants.lowerConfig),
                new SparkSetup(10, ArmConstants.upperConfig)
        );

        ArmControl armControl = armControlSetup.build(path.addChild("arm-control"));

        ArmInput armInput = new ArmInput(new Joystick(1));

        ArmSubsystem armSubsystem = new ArmSubsystem(armInput, armControl, path.generateStringLogger("arm-subsystem"));

        //SYSTEMS_GREEN.setOn(); //LET'S WIN SOME DAMN REGIONALS!!S

        return new RobotContainer(driveSubsystem, armSubsystem);
    }

    /**
     * Build a new neo/mk4I based drive control
     *
     * @param path
     * @return
     */
    private static DriveControl buildNeoDriveControl(ProcessPath path) {
        // used to configure the spark motor in SparkSetup
        MotorConfig driveMotorConfig = new MotorConfig(
                DriveConstants.MK4I_L2.getDriveReduction(),
                1,
                DriveConstants.MK4I_L2.getWheelDiameter(),
                true,
                true,
                20,
                false,
                false,
                Optional.empty()
        );

        // we know the PID constants from swervelib, so set them here
        MotorConfig steerMotorConfig = new MotorConfig(
                DriveConstants.MK4I_L2.getSteerReduction(),
                1,
                DriveConstants.MK4I_L2.getWheelDiameter(),
                true,
                true,
                20,
                false,
                false,
                1,
                0,
                .1
        );

        DriveControl driveControl = new DriveControlSetup(
                new SwerveModuleSetup(
                        new DriveControllerSetup(new SparkDriveMotorSetup(DriveConstants.frontLeftModuleDriveMotor_ID, driveMotorConfig, DriveConstants.MK4I_L2)),
                        new SteerControllerSetup(
                                new SparkSteerMotorSetup(DriveConstants.frontLeftModuleSteerMotor_ID, steerMotorConfig, DriveConstants.MK4I_L2),
                                new ThriftyEncoderSetup(DriveConstants.frontLeftModuleSteerEncoder_CHANNEL, DriveConstants.frontLeftModuleSteerOffset))
                ),
                new SwerveModuleSetup(
                        new DriveControllerSetup(new SparkDriveMotorSetup(DriveConstants.frontRightModuleDriveMotor_ID, driveMotorConfig, DriveConstants.MK4I_L2)),
                        new SteerControllerSetup(
                                new SparkSteerMotorSetup(DriveConstants.frontRightModuleSteerMotor_ID, steerMotorConfig, DriveConstants.MK4I_L2),
                                new ThriftyEncoderSetup(DriveConstants.frontRightModuleSteerEncoder_CHANNEL, DriveConstants.frontRightModuleSteerOffset))
                ),
                new SwerveModuleSetup(
                        new DriveControllerSetup(new SparkDriveMotorSetup(DriveConstants.backLeftModuleDriveMotor_ID, driveMotorConfig, DriveConstants.MK4I_L2)),
                        new SteerControllerSetup(
                                new SparkSteerMotorSetup(DriveConstants.backLeftModuleSteerMotor_ID, steerMotorConfig, DriveConstants.MK4I_L2),
                                new ThriftyEncoderSetup(DriveConstants.backLeftModuleSteerEncoder_CHANNEL, DriveConstants.backLeftModuleSteerOffset))
                ),
                new SwerveModuleSetup(
                        new DriveControllerSetup(new SparkDriveMotorSetup(DriveConstants.backRightModuleDriveMotor_ID, driveMotorConfig, DriveConstants.MK4I_L2)),
                        new SteerControllerSetup(
                                new SparkSteerMotorSetup(DriveConstants.backRightModuleSteerMotor_ID, steerMotorConfig, DriveConstants.MK4I_L2),
                                new ThriftyEncoderSetup(DriveConstants.backRightModuleSteerEncoder_CHANNEL, DriveConstants.backRightModuleSteerOffset))
                ),
                new WPI_PigeonIMU(5)
        ).build(path.addChild("drive-control"));

        REVPhysicsSim.getInstance().run();
        return driveControl;
    }

    /**
     * Build a new talon/mk4 based drive control
     *
     * @param path
     * @return
     */
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

        double frontLeftModuleSteerOffset = -Math.toRadians(232.55); // set front left steer offset

        double frontRightModuleSteerOffset = -Math.toRadians(331.96 - 180); // set front right steer offset

        double backLeftModuleSteerOffset = -Math.toRadians(255.49); // set back left steer offset

        double backRightModuleSteerOffset = -Math.toRadians(70.66 + 180); // set back right steer offset

        double sensorPositionCoefficient = 2.0 * Math.PI / 2048 * DriveConstants.MK4_L2.getSteerReduction();

        return new DriveControlSetup(
                new SwerveModuleSetup(
                        new DriveControllerSetup(new TalonDriveMotorSetup(frontLeftModuleDriveMotor_ID, DriveConstants.MK4_L2)),
                        new SteerControllerSetup(
                                new TalonSteerMotorSetup(frontLeftModuleSteerMotor_ID, DriveConstants.MK4_L2),
                                new CANCoderAbsoluteEncoderSetup(frontLeftModuleSteerEncoder_ID, frontRightModuleSteerOffset),
                                sensorPositionCoefficient
                        )
                ),
                new SwerveModuleSetup(
                        new DriveControllerSetup(new TalonDriveMotorSetup(frontRightModuleDriveMotor_ID, DriveConstants.MK4_L2)),
                        new SteerControllerSetup(
                                new TalonSteerMotorSetup(frontRightModuleSteerMotor_ID, DriveConstants.MK4_L2),
                                new CANCoderAbsoluteEncoderSetup(frontRightModuleSteerEncoder_ID, frontLeftModuleSteerOffset),
                                sensorPositionCoefficient
                        )
                ),
                new SwerveModuleSetup(
                        new DriveControllerSetup(new TalonDriveMotorSetup(backLeftModuleDriveMotor_ID, DriveConstants.MK4_L2)),
                        new SteerControllerSetup(
                                new TalonSteerMotorSetup(backLeftModuleSteerMotor_ID, DriveConstants.MK4_L2),
                                new CANCoderAbsoluteEncoderSetup(backLeftModuleSteerEncoder_ID, backLeftModuleSteerOffset),
                                sensorPositionCoefficient
                        )
                ),
                new SwerveModuleSetup(
                        new DriveControllerSetup(new TalonDriveMotorSetup(backRightModuleDriveMotor_ID, DriveConstants.MK4_L2)),
                        new SteerControllerSetup(
                                new TalonSteerMotorSetup(backRightModuleSteerMotor_ID, DriveConstants.MK4_L2),
                                new CANCoderAbsoluteEncoderSetup(backRightModuleSteerEncoder_ID, backRightModuleSteerOffset),
                                sensorPositionCoefficient
                        )
                ),
                new WPI_PigeonIMU(5)
        ).build(path.addChild("drive-control"));
    }

}
