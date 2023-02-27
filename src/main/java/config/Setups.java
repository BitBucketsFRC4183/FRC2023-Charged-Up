package config;

import org.bitbuckets.auto.*;
import org.bitbuckets.drive.controlsds.DriveControl;
import org.bitbuckets.drive.controlsds.DriveControlSetup;
import org.bitbuckets.drive.controlsds.sds.*;
import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.SwitcherSetup;
import org.bitbuckets.lib.hardware.IAbsoluteEncoder;
import org.bitbuckets.lib.hardware.IMotorController;
import org.bitbuckets.lib.util.MockingUtil;
import org.bitbuckets.lib.vendor.ctre.CANCoderAbsoluteEncoderSetup;
import org.bitbuckets.lib.vendor.ctre.TalonDriveMotorSetup;
import org.bitbuckets.lib.vendor.ctre.TalonSteerMotorSetup;
import org.bitbuckets.lib.vendor.sim.dc.DCSimSetup;
import org.bitbuckets.lib.vendor.spark.SparkDriveMotorSetup;
import org.bitbuckets.lib.vendor.spark.SparkSetup;
import org.bitbuckets.lib.vendor.spark.SparkSteerMotorSetup;
import org.bitbuckets.lib.vendor.thrifty.ThriftyEncoderSetup;
import org.bitbuckets.vision.IVisionControl;
import org.bitbuckets.vision.VisionControlSetup;

public interface Setups {


    ISetup<IMotorController> FRONT_LEFT_DRIVE = new SwitcherSetup<>(
            //use this on appa
            new TalonDriveMotorSetup(
                    MotorIds.FRONT_LEFT_DRIVE_APPA,
                    DriveAppaSpecific.MK4_L2
            ),
            //use this on turd
            new SparkDriveMotorSetup(
                    MotorIds.FRONT_LEFT_DRIVE_ID,
                    DriveTurdSpecific.DRIVE_TURD,
                    DriveTurdSpecific.MK4I_L2
            ),
            new DCSimSetup(
                    DriveTurdSpecific.DRIVE_TURD,
                    Drive.DRIVE_DC_CONFIG,
                    Drive.DRIVE_SIM_PID
            )
    );

    ISetup<IMotorController> FRONT_RIGHT_DRIVE = new SwitcherSetup<>(
            //use this on appa
            new TalonDriveMotorSetup(
                    MotorIds.FRONT_RIGHT_DRIVE_APPA,
                    DriveAppaSpecific.MK4_L2
            ),
            //use this on turd
            new SparkDriveMotorSetup(
                    MotorIds.FRONT_RIGHT_DRIVE_ID,
                    DriveTurdSpecific.DRIVE_TURD,
                    DriveTurdSpecific.MK4I_L2
            ),
            new DCSimSetup(
                    DriveTurdSpecific.DRIVE_TURD,
                    Drive.DRIVE_DC_CONFIG,
                    Drive.DRIVE_SIM_PID
            )
    );
    ISetup<IMotorController> BACK_LEFT_DRIVE = new SwitcherSetup<>(
            //use this on appa
            new TalonDriveMotorSetup(
                    MotorIds.BACK_LEFT_DRIVE_APPA,
                    DriveAppaSpecific.MK4_L2
            ),
            //use this on turd
            new SparkDriveMotorSetup(
                    MotorIds.BACK_LEFT_DRIVE_ID,
                    DriveTurdSpecific.DRIVE_TURD,
                    DriveTurdSpecific.MK4I_L2
            ),
            new DCSimSetup(
                    DriveTurdSpecific.DRIVE_TURD,
                    Drive.DRIVE_DC_CONFIG,
                    Drive.DRIVE_SIM_PID
            )
    );
    ISetup<IMotorController> BACK_RIGHT_DRIVE = new SwitcherSetup<>(
            //use this on appa
            new TalonDriveMotorSetup(
                    MotorIds.BACK_RIGHT_DRIVE_APPA,
                    DriveAppaSpecific.MK4_L2
            ),
            //use this on turd
            new SparkDriveMotorSetup(
                    MotorIds.BACK_RIGHT_DRIVE_ID,
                    DriveTurdSpecific.DRIVE_TURD,
                    DriveTurdSpecific.MK4I_L2
            ),
            new DCSimSetup(
                    DriveTurdSpecific.DRIVE_TURD,
                    Drive.DRIVE_DC_CONFIG,
                    Drive.DRIVE_SIM_PID
            )
    );

    ISetup<IDriveController> FRONT_LEFT_DRIVE_CTRL = new DriveControllerSetup(FRONT_LEFT_DRIVE);
    ISetup<IDriveController> FRONT_RIGHT_DRIVE_CTRL = new DriveControllerSetup(FRONT_RIGHT_DRIVE);
    ISetup<IDriveController> BACK_LEFT_DRIVE_CTRL = new DriveControllerSetup(BACK_LEFT_DRIVE);
    ISetup<IDriveController> BACK_RIGHT_DRIVE_CTRL = new DriveControllerSetup(BACK_RIGHT_DRIVE);

    //STEER MOTORS
    ISetup<IMotorController> FRONT_LEFT_STEER = new SwitcherSetup<>(
            //use this on appa
            new TalonSteerMotorSetup(
                    MotorIds.FRONT_LEFT_STEER_APPA,
                    DriveAppaSpecific.MK4_L2
            ),
            //use this on turd
            new SparkSteerMotorSetup(
                    MotorIds.FRONT_LEFT_STEER_ID,
                    DriveAppaSpecific.STEER_APPA,
                    Drive.STEER_PID,
                    DriveTurdSpecific.MK4I_L2
            ),
            new DCSimSetup(
                    DriveTurdSpecific.STEER_TURD,
                    Drive.STEER_DC_CONFIG,
                    Drive.STEER_SIM_PID
            )
    );

    ISetup<IMotorController> FRONT_RIGHT_STEER = new SwitcherSetup<>(
            //use this on appa
            new TalonSteerMotorSetup(
                    MotorIds.FRONT_RIGHT_STEER_APPA,
                    DriveAppaSpecific.MK4_L2
            ),
            //use this on turd
            new SparkSteerMotorSetup(
                    MotorIds.FRONT_RIGHT_STEER_ID,
                    DriveAppaSpecific.STEER_APPA,
                    Drive.STEER_PID,
                    DriveTurdSpecific.MK4I_L2
            ),
            new DCSimSetup(
                    DriveTurdSpecific.STEER_TURD,
                    Drive.STEER_DC_CONFIG,
                    Drive.STEER_SIM_PID
            )
    );

    ISetup<IMotorController> BACK_LEFT_STEER = new SwitcherSetup<>(
            //use this on appa
            new TalonSteerMotorSetup(
                    MotorIds.BACK_LEFT_STEER_APPA,
                    DriveAppaSpecific.MK4_L2
            ),
            //use this on turd
            new SparkSteerMotorSetup(
                    MotorIds.BACK_LEFT_STEER_ID,
                    DriveAppaSpecific.STEER_APPA,
                    Drive.STEER_PID,
                    DriveTurdSpecific.MK4I_L2
            ),
            new DCSimSetup(
                    DriveTurdSpecific.STEER_TURD,
                    Drive.STEER_DC_CONFIG,
                    Drive.STEER_SIM_PID
            )
    );

    ISetup<IMotorController> BACK_RIGHT_STEER = new SwitcherSetup<>(
            //use this on appa
            new TalonSteerMotorSetup(
                    MotorIds.BACK_RIGHT_STEER_APPA,
                    DriveAppaSpecific.MK4_L2
            ),
            //use this on turd
            new SparkSteerMotorSetup(
                    MotorIds.BACK_RIGHT_STEER_ID,
                    DriveAppaSpecific.STEER_APPA,
                    Drive.STEER_PID,
                    DriveTurdSpecific.MK4I_L2
            ),
            new DCSimSetup(
                    DriveTurdSpecific.STEER_TURD,
                    Drive.STEER_DC_CONFIG,
                    Drive.STEER_SIM_PID
            )
    );

    //ABSOLUTE ENCODERS

    ISetup<IAbsoluteEncoder> FRONT_LEFT_ABS_ENCODER = new SwitcherSetup<>(
            new CANCoderAbsoluteEncoderSetup(
                    MotorIds.FRONT_LEFT_ENCODER_CHANNEL_APPA,
                    DriveAppaSpecific.FRONT_LEFT_OFFSET_APPA
            ),
            new ThriftyEncoderSetup(
                    MotorIds.FRONT_LEFT_ENCODER_CHANNEL,
                    DriveTurdSpecific.FRONT_LEFT_OFFSET_TURD
            ),
            MockingUtil.noops(IAbsoluteEncoder.class)
    );
    ISetup<IAbsoluteEncoder> FRONT_RIGHT_ABS_ENCODER = new SwitcherSetup<>(
            new CANCoderAbsoluteEncoderSetup(
                    MotorIds.FRONT_RIGHT_ENCODER_CHANNEL_APPA,
                    DriveAppaSpecific.FRONT_RIGHT_OFFSET_APPA
            ),
            new ThriftyEncoderSetup(
                    MotorIds.FRONT_RIGHT_ENCODER_CHANNEL,
                    DriveTurdSpecific.FRONT_RIGHT_OFFSET_TURD
            ),
            MockingUtil.noops(IAbsoluteEncoder.class)
    );
    ISetup<IAbsoluteEncoder> BACK_LEFT_ABS_ENCODER = new SwitcherSetup<>(
            new CANCoderAbsoluteEncoderSetup(
                    MotorIds.BACK_LEFT_ENCODER_CHANNEL_APPA,
                    DriveAppaSpecific.BACK_LEFT_OFFSET_APPA
            ),
            new ThriftyEncoderSetup(
                    MotorIds.BACK_LEFT_ENCODER_CHANNEL,
                    DriveTurdSpecific.BACK_LEFT_OFFSET_TURD
            ),
            MockingUtil.noops(IAbsoluteEncoder.class)
    );
    ISetup<IAbsoluteEncoder> BACK_RIGHT_ABS_ENCODER = new SwitcherSetup<>(
            new CANCoderAbsoluteEncoderSetup(
                    MotorIds.BACK_RIGHT_ENCODER_CHANNEL_APPA,
                    DriveAppaSpecific.BACK_RIGHT_OFFSET_APPA
            ),
            new ThriftyEncoderSetup(
                    MotorIds.BACK_RIGHT_ENCODER_CHANNEL,
                    DriveTurdSpecific.BACK_RIGHT_OFFSET_TURD
            ),
            MockingUtil.noops(IAbsoluteEncoder.class)
    );

    //STEER CONTROLLERS

    ISetup<ISteerController> FRONT_LEFT_STEER_CTRL = new SteerControllerSetup(
            FRONT_LEFT_STEER,
            FRONT_LEFT_ABS_ENCODER
    );
    ISetup<ISteerController> FRONT_RIGHT_STEER_CTRL = new SteerControllerSetup(
            FRONT_RIGHT_STEER,
            FRONT_RIGHT_ABS_ENCODER
    );
    ISetup<ISteerController> BACK_LEFT_STEER_CTRL = new SteerControllerSetup(
            BACK_LEFT_STEER,
            BACK_LEFT_ABS_ENCODER
    );
    ISetup<ISteerController> BACK_RIGHT_STEER_CTRL = new SteerControllerSetup(
            BACK_RIGHT_STEER,
            BACK_RIGHT_ABS_ENCODER
    );

    //SWERVE MODULES

    ISetup<ISwerveModule> FRONT_LEFT = new SwerveModuleSetup(
            FRONT_LEFT_DRIVE_CTRL,
            FRONT_LEFT_STEER_CTRL
    );
    ISetup<ISwerveModule> FRONT_RIGHT = new SwerveModuleSetup(
            FRONT_RIGHT_DRIVE_CTRL,
            FRONT_RIGHT_STEER_CTRL
    );
    ISetup<ISwerveModule> BACK_LEFT = new SwerveModuleSetup(
            BACK_LEFT_DRIVE_CTRL,
            BACK_LEFT_STEER_CTRL
    );
    ISetup<ISwerveModule> BACK_RIGHT = new SwerveModuleSetup(
            BACK_RIGHT_DRIVE_CTRL,
            BACK_RIGHT_STEER_CTRL
    );

    //ARM

    ISetup<IMotorController> LOWER_ARM = new SwitcherSetup<>(
            MockingUtil.noops(IMotorController.class),
            new SparkSetup(
                    9,


            ),
    )


    //CONTROLS

    ISetup<DriveControl> DRIVE_CONTROL = new DriveControlSetup(
            FRONT_LEFT,
            FRONT_RIGHT,
            BACK_LEFT,
            BACK_RIGHT
    );
    ISetup<IAutoControl> AUTO_CONTROL = new AutoControlSetup();
    ISetup<IVisionControl> VISION_CONTROL = new VisionControlSetup(false);





}
