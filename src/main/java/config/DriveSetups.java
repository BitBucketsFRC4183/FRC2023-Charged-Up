package config;

import org.bitbuckets.drive.ISwerveModule;
import org.bitbuckets.drive.balance.BalanceControl;
import org.bitbuckets.drive.balance.BalanceSetup;
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
    ISetup<IMotorController> FL_STEER_APPA = new TalonSteerMotorSetup(
            MotorIds.FRONT_LEFT_STEER_APPA,
            DriveAppaSpecific.MK4_L2
    );

    ISetup<IMotorController> FL_STEER_NEW = new SparkSteerMotorSetup(
            MotorIds.FRONT_LEFT_STEER_ID,
            DriveTurdSpecific.STEER_TURD,
            Drive.STEER_PID,
            DriveTurdSpecific.MK4I_L2
    );

    ISetup<IMotorController> FL_STEER_SIM =  new DCSimSetup(
            DriveTurdSpecific.STEER_TURD,
            Drive.STEER_DC_CONFIG,
            Drive.STEER_SIM_PID
    );

    ISetup<IMotorController> FR_STEER_APPA = new TalonSteerMotorSetup(
            MotorIds.FRONT_RIGHT_STEER_APPA,
            DriveAppaSpecific.MK4_L2
    );

    ISetup<IMotorController> FR_STEER_NEW = new SparkSteerMotorSetup(
            MotorIds.FRONT_RIGHT_STEER_ID,
            DriveAppaSpecific.STEER_APPA,
            Drive.STEER_PID,
            DriveTurdSpecific.MK4I_L2
    );

    ISetup<IMotorController> FR_STEER_SIM = new DCSimSetup(
            DriveTurdSpecific.STEER_TURD,
            Drive.STEER_DC_CONFIG,
            Drive.STEER_SIM_PID
    );

    ISetup<IMotorController> BL_STEER_APPA = new TalonSteerMotorSetup(
            MotorIds.BACK_LEFT_STEER_APPA,
            DriveAppaSpecific.MK4_L2
    );

    ISetup<IMotorController> BL_STEER_NEW = new SparkSteerMotorSetup(
            MotorIds.BACK_LEFT_STEER_ID,
            DriveAppaSpecific.STEER_APPA,
            Drive.STEER_PID,
            DriveTurdSpecific.MK4I_L2
    );

    ISetup<IMotorController> BL_STEER_SIM = new DCSimSetup(
            DriveTurdSpecific.STEER_TURD,
            Drive.STEER_DC_CONFIG,
            Drive.STEER_SIM_PID
    );

    ISetup<IMotorController> BR_STEER_APPA = new TalonSteerMotorSetup(
            MotorIds.BACK_RIGHT_STEER_APPA,
            DriveAppaSpecific.MK4_L2
    );

    ISetup<IMotorController> BR_STEER_NEW = new SparkSteerMotorSetup(
            MotorIds.BACK_RIGHT_STEER_ID,
            DriveAppaSpecific.STEER_APPA,
            Drive.STEER_PID,
            DriveTurdSpecific.MK4I_L2
    );

    ISetup<IMotorController> BR_STEER_SIM = new DCSimSetup(
            DriveTurdSpecific.STEER_TURD,
            Drive.STEER_DC_CONFIG,
            Drive.STEER_SIM_PID
    );



    //ABSOLUTE ENCODERS

    ISetup<IAbsoluteEncoder> FL_ABS_ENCODER = new SwapSetup<>(
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
    ISetup<IAbsoluteEncoder> FR_ABS_ENCODER = new SwapSetup<>(
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
    ISetup<IAbsoluteEncoder> BL_ABS_ENCODER = new SwapSetup<>(
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
    ISetup<IAbsoluteEncoder> BR_ABS_ENCODER = new SwapSetup<>(
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

    double COEFFICIENT = 2.0 * Math.PI / 2048 * DriveAppaSpecific.MK4_L2.getSteerReduction();

    ISetup<ISteerController> FL_STEER_CTRL = new SwapSetup<>(
            new SteerControllerSetup(
                    FL_STEER_APPA,
                    FL_ABS_ENCODER,
                    COEFFICIENT
            ),
            new SteerControllerSetup(
                    FL_STEER_NEW,
                    FL_ABS_ENCODER
            ),
            new SteerControllerSetup(
                    FL_STEER_SIM,
                    FL_ABS_ENCODER
            )
    );

    ISetup<ISteerController> FR_STEER_CTRL = new SwapSetup<>(
            new SteerControllerSetup(
                    FR_STEER_APPA,
                    FR_ABS_ENCODER,
                    COEFFICIENT
            ),
            new SteerControllerSetup(
                    FR_STEER_NEW,
                    FR_ABS_ENCODER
            ),
            new SteerControllerSetup(
                    FR_STEER_SIM,
                    FR_ABS_ENCODER
            )
    );

    ISetup<ISteerController> BR_STEER_CTRL = new SwapSetup<>(
            new SteerControllerSetup(
                    BR_STEER_APPA,
                    BR_ABS_ENCODER,
                    COEFFICIENT
            ),
            new SteerControllerSetup(
                    BR_STEER_NEW,
                    BR_ABS_ENCODER
            ),
            new SteerControllerSetup(
                    BR_STEER_SIM,
                    BR_ABS_ENCODER
            )
    );

    ISetup<ISteerController> BL_STEER_CTRL = new SwapSetup<>(
            new SteerControllerSetup(
                    BL_STEER_APPA,
                    BL_ABS_ENCODER,
                    COEFFICIENT
            ),
            new SteerControllerSetup(
                    BL_STEER_NEW,
                    BL_ABS_ENCODER
            ),
            new SteerControllerSetup(
                    BL_STEER_SIM,
                    BL_ABS_ENCODER
            )
    );



    //SWERVE MODULES

    ISetup<ISwerveModule> FRONT_LEFT = new SwerveModuleSetup(
            FRONT_LEFT_DRIVE_CTRL,
            FL_STEER_CTRL
    );
    ISetup<ISwerveModule> FRONT_RIGHT = new SwerveModuleSetup(
            FRONT_RIGHT_DRIVE_CTRL,
            FR_STEER_CTRL
    );
    ISetup<ISwerveModule> BACK_LEFT = new SwerveModuleSetup(
            BACK_LEFT_DRIVE_CTRL,
            BL_STEER_CTRL
    );
    ISetup<ISwerveModule> BACK_RIGHT = new SwerveModuleSetup(
            BACK_RIGHT_DRIVE_CTRL,
            BR_STEER_CTRL
    );


    ISetup<IPIDCalculator> BALANCE_CONTROLLER = new PIDCalculatorSetup(Drive.DRIVE_BALANCE_PID);
    ISetup<BalanceControl> BALANCE_SETUP = new BalanceSetup(BALANCE_CONTROLLER);
}
