package config;

import org.bitbuckets.drive.balance.BalanceControl;
import org.bitbuckets.drive.balance.BalanceSetup;
import org.bitbuckets.drive.controlsds.sds.*;
import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.SwapSetup;
import org.bitbuckets.lib.control.IPIDCalculator;
import org.bitbuckets.lib.control.PIDCalculatorSetup;
import org.bitbuckets.lib.hardware.IAbsoluteEncoder;
import org.bitbuckets.lib.hardware.IMotorController;
import org.bitbuckets.lib.vendor.ctre.CANCoderAbsoluteSetup;
import org.bitbuckets.lib.vendor.ctre.TalonDriveMotorSetup;
import org.bitbuckets.lib.vendor.ctre.TalonSteerMotorSetup;

import org.bitbuckets.lib.vendor.noops.NoopsAbsoluteEncoder;
import org.bitbuckets.lib.vendor.sim.dc.DCSimSetup;
import org.bitbuckets.lib.vendor.spark.SparkDriveMotorSetup;
import org.bitbuckets.lib.vendor.spark.SparkSteerMotorSetup;
import org.bitbuckets.lib.vendor.thrifty.ThriftyEncoderSetup;

public interface DriveSetups {


    ISetup<IMotorController> FRONT_LEFT_DRIVE = new SwapSetup<>(
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

    ISetup<IMotorController> FRONT_RIGHT_DRIVE = new SwapSetup<>(
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
    ISetup<IMotorController> BACK_LEFT_DRIVE = new SwapSetup<>(
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
    ISetup<IMotorController> BACK_RIGHT_DRIVE = new SwapSetup<>(
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
    ISetup<IMotorController> FRONT_LEFT_STEER = new SwapSetup<>(
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

    ISetup<IMotorController> FRONT_RIGHT_STEER = new SwapSetup<>(
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

    ISetup<IMotorController> BACK_LEFT_STEER = new SwapSetup<>(
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

    ISetup<IMotorController> BACK_RIGHT_STEER = new SwapSetup<>(
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

    ISetup<IAbsoluteEncoder> FRONT_LEFT_ABS_ENCODER = new SwapSetup<>(
            new CANCoderAbsoluteSetup(
                    MotorIds.FRONT_LEFT_ENCODER_CHANNEL_APPA,
                    DriveAppaSpecific.FRONT_LEFT_OFFSET_APPA
            ),
            new ThriftyEncoderSetup(
                    MotorIds.FRONT_LEFT_ENCODER_CHANNEL,
                    DriveTurdSpecific.FRONT_LEFT_OFFSET_TURD
            ),
            NoopsAbsoluteEncoder.SETUP
    );
    ISetup<IAbsoluteEncoder> FRONT_RIGHT_ABS_ENCODER = new SwapSetup<>(
            new CANCoderAbsoluteSetup(
                    MotorIds.FRONT_RIGHT_ENCODER_CHANNEL_APPA,
                    DriveAppaSpecific.FRONT_RIGHT_OFFSET_APPA
            ),
            new ThriftyEncoderSetup(
                    MotorIds.FRONT_RIGHT_ENCODER_CHANNEL,
                    DriveTurdSpecific.FRONT_RIGHT_OFFSET_TURD
            ),
            NoopsAbsoluteEncoder.SETUP
    );
    ISetup<IAbsoluteEncoder> BACK_LEFT_ABS_ENCODER = new SwapSetup<>(
            new CANCoderAbsoluteSetup(
                    MotorIds.BACK_LEFT_ENCODER_CHANNEL_APPA,
                    DriveAppaSpecific.BACK_LEFT_OFFSET_APPA
            ),
            new ThriftyEncoderSetup(
                    MotorIds.BACK_LEFT_ENCODER_CHANNEL,
                    DriveTurdSpecific.BACK_LEFT_OFFSET_TURD
            ),
            NoopsAbsoluteEncoder.SETUP
    );
    ISetup<IAbsoluteEncoder> BACK_RIGHT_ABS_ENCODER = new SwapSetup<>(
            new CANCoderAbsoluteSetup(
                    MotorIds.BACK_RIGHT_ENCODER_CHANNEL_APPA,
                    DriveAppaSpecific.BACK_RIGHT_OFFSET_APPA
            ),
            new ThriftyEncoderSetup(
                    MotorIds.BACK_RIGHT_ENCODER_CHANNEL,
                    DriveTurdSpecific.BACK_RIGHT_OFFSET_TURD
            ),
            NoopsAbsoluteEncoder.SETUP
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


    ISetup<IPIDCalculator> BALANCE_CONTROLLER = new PIDCalculatorSetup(Drive.DRIVE_BALANCE_PID);
    ISetup<BalanceControl> BALANCE_SETUP = new BalanceSetup(BALANCE_CONTROLLER);
}
