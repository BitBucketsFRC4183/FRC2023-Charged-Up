package org.bitbuckets.drive.controlsds;

import com.ctre.phoenix.motorcontrol.*;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.ctre.phoenix.motorcontrol.can.TalonFXConfiguration;
import com.ctre.phoenix.sensors.*;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInLayouts;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardLayout;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.bitbuckets.drive.DriveSDSConstants;
import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.ProcessPath;
import org.bitbuckets.lib.log.DataLogger;

import static org.bitbuckets.drive.controlsds.CtreUtils.checkCtreError;

/**
 * Sets up prereqs for a drive controller
 * <p>
 * really fucking simple because a drivecontrol is super simple LMAO
 */
public class DriveControlSDSSetup implements ISetup<DriveControlSDS> {

    ModuleConfiguration MK4_L2 = new ModuleConfiguration(
            0.10033,
            (14.0 / 50.0) * (27.0 / 17.0) * (15.0 / 45.0),
            true,
            (15.0 / 32.0) * (10.0 / 60.0),
            true
    );

    private double nominalVoltage = 12.0;
    private double driveCurrentLimit = 80.0;
    private double steerCurrentLimit = 20.0;

    private double proportionalConstant = .2;
    private double integralConstant = 0;
    private double derivativeConstant = .1;
    private static final int CAN_TIMEOUT_MS = 250;
    private static final int STATUS_FRAME_GENERAL_PERIOD_MS = 250;
    private static final double TICKS_PER_ROTATION = 2048.0;

    int canCoderPeriodMilliseconds = 100;

    @Override
    public DriveControlSDS build(ProcessPath path) {
        DataLogger<DriveControlSDSDataAutoGen> logger = path.generatePushDataLogger(DriveControlSDSDataAutoGen::new);

        double wheelWearFactor = 1;

        double maxVelocity_metersPerSecond =
                6380.0 /
                        60.0 *
                        MK4_L2.getDriveReduction() *
                        (MK4_L2.getWheelDiameter() * wheelWearFactor) *
                        Math.PI;

        double maxAngularVelocity_radiansPerSecond =
                maxVelocity_metersPerSecond /
                        Math.hypot(DriveSDSConstants.drivetrainTrackWidth_meters / 2.0, DriveSDSConstants.drivetrainWheelBase_meters / 2.0);

        SmartDashboard.putNumber("/drivetrain/max_angular_velocity", maxAngularVelocity_radiansPerSecond);

        Translation2d moduleFrontLeftLocation =
                new Translation2d(DriveSDSConstants.drivetrainTrackWidth_meters / 2.0, DriveSDSConstants.drivetrainWheelBase_meters / 2.0);
        Translation2d moduleFrontRightLocation =
                new Translation2d(DriveSDSConstants.drivetrainTrackWidth_meters / 2.0, -DriveSDSConstants.drivetrainWheelBase_meters / 2.0);
        Translation2d moduleBackLeftLocation =
                new Translation2d(-DriveSDSConstants.drivetrainTrackWidth_meters / 2.0, DriveSDSConstants.drivetrainWheelBase_meters / 2.0);
        Translation2d moduleBackRightLocation =
                new Translation2d(
                        -DriveSDSConstants.drivetrainTrackWidth_meters / 2.0,
                        -DriveSDSConstants.drivetrainWheelBase_meters / 2.0
                );

        SwerveDriveKinematics kinematics =
                new SwerveDriveKinematics(
                        moduleFrontLeftLocation,
                        moduleFrontRightLocation,
                        moduleBackLeftLocation,
                        moduleBackRightLocation
                );

        WPI_PigeonIMU gyro = new WPI_PigeonIMU(5);


        ShuffleboardTab tab = Shuffleboard.getTab("Drivetrain");

        // There are 4 methods you can call to create your swerve modules.
        // The method you use depends on what motors you are using.
        //
        // Mk3SwerveModuleHelper.createFalcon500(...)
        // Your module has two Falcon 500s on it. One for steering and one for driving.
        //
        // Similar helpers also exist for Mk4 modules using the Mk4SwerveModuleHelper class.

        // By default we will use Falcon 500s in standard configuration. But if you use
        // a different configuration or motors
        // you MUST change it. If you do not, your code will crash on startup.
        // Setup motor configuration
        SwerveModule moduleFrontLeft = createSwerveModule(
                //Smart Dashboard Tab
                tab.getLayout("Front Left Module", BuiltInLayouts.kList).withSize(2, 4).withPosition(0, 0),
                DriveSDSConstants.frontLeftModuleDriveMotor_ID, //Drive Motor
                DriveSDSConstants.frontLeftModuleSteerMotor_ID, //Steer Motor
                DriveSDSConstants.frontLeftModuleSteerEncoder_ID, //Steer Encoder
                DriveSDSConstants.frontLeftModuleSteerOffset //Steer Offset
        );

        SwerveModule moduleFrontRight = createSwerveModule(
                //Smart Dashboard Tab
                tab.getLayout("Front Right Module", BuiltInLayouts.kList).withSize(2, 4).withPosition(2, 0),
                DriveSDSConstants.frontRightModuleDriveMotor_ID, //Drive Motor
                DriveSDSConstants.frontRightModuleSteerMotor_ID, //Steer Motor
                DriveSDSConstants.frontRightModuleSteerEncoder_ID, //Steer Encoder
                DriveSDSConstants.frontRightModuleSteerOffset //Steer Offset
        );

        SwerveModule moduleBackLeft = createSwerveModule(
                //Smart Dashboard Tab
                tab.getLayout("Back Left Module", BuiltInLayouts.kList).withSize(2, 4).withPosition(4, 0),
                DriveSDSConstants.backLeftModuleDriveMotor_ID, //Drive Motor
                DriveSDSConstants.backLeftModuleSteerMotor_ID, //Steer Motor
                DriveSDSConstants.backLeftModuleSteerEncoder_ID, //Steer Encoder
                DriveSDSConstants.backLeftModuleSteerOffset //Steer Offset
        );

        SwerveModule moduleBackRight = createSwerveModule(
                //Smart Dashboard Tab
                tab.getLayout("Back Right Module", BuiltInLayouts.kList).withSize(2, 4).withPosition(6, 0),
                DriveSDSConstants.backRightModuleDriveMotor_ID, //Drive Motor
                DriveSDSConstants.backRightModuleSteerMotor_ID, //Steer Motor
                DriveSDSConstants.backRightModuleSteerEncoder_ID, //Steer Encoder
                DriveSDSConstants.backRightModuleSteerOffset //Steer Offset
        );


        //Calibrate the gyro only once when the drive subsystem is first initialized
        gyro.calibrate();

        DriveControlSDS control = new DriveControlSDS(logger, maxVelocity_metersPerSecond, maxAngularVelocity_radiansPerSecond,
                gyro, moduleFrontLeft, moduleFrontRight, moduleBackLeft, moduleBackRight, kinematics);


        path.registerLoop(control::guaranteedLoggingLoop, "logging");

        return control;
    }

    SwerveModule createSwerveModule(
            ShuffleboardLayout container,
            int driveMotorPort,
            int steerMotorPort,
            int steerEncoderPort,
            double steerOffset
    ) {
        var driveController = createDriveController(driveMotorPort, MK4_L2);
        var steerController = createSteerController(steerMotorPort, steerEncoderPort, steerOffset, MK4_L2);
        return new ModuleImplementation(driveController, steerController);
    }

    DriveController createDriveController(int driveMotorPort, ModuleConfiguration moduleConfiguration) {
        TalonFXConfiguration motorConfiguration = new TalonFXConfiguration();

        double sensorPositionCoefficient = Math.PI * moduleConfiguration.getWheelDiameter() * moduleConfiguration.getDriveReduction() / TICKS_PER_ROTATION;
        double sensorVelocityCoefficient = sensorPositionCoefficient * 10.0;

        motorConfiguration.voltageCompSaturation = nominalVoltage;

        motorConfiguration.supplyCurrLimit.currentLimit = driveCurrentLimit;
        motorConfiguration.supplyCurrLimit.enable = true;

        TalonFX motor = new TalonFX(driveMotorPort);
        checkCtreError(motor.configAllSettings(motorConfiguration), "Failed to configure Falcon 500");

        // Enable voltage compensation
        motor.enableVoltageCompensation(true);

        motor.setNeutralMode(NeutralMode.Brake);

        motor.setInverted(moduleConfiguration.isDriveInverted() ? TalonFXInvertType.Clockwise : TalonFXInvertType.CounterClockwise);
        motor.setSensorPhase(true);

        // Reduce CAN status frame rates
        checkCtreError(
                motor.setStatusFramePeriod(
                        StatusFrameEnhanced.Status_1_General,
                        STATUS_FRAME_GENERAL_PERIOD_MS,
                        CAN_TIMEOUT_MS
                ),
                "Failed to configure Falcon status frame period"
        );

        return new Falcon500DriveController(motor, sensorVelocityCoefficient, nominalVoltage);
    }

    SteerController createSteerController(int steerMotorPort, int steerEncoderPort, double steerOffset, ModuleConfiguration moduleConfiguration) {

        AbsoluteEncoder absoluteEncoder = createAbsoluteEncoder(steerEncoderPort, steerOffset);

        final double sensorPositionCoefficient = 2.0 * Math.PI / TICKS_PER_ROTATION * moduleConfiguration.getSteerReduction();
        final double sensorVelocityCoefficient = sensorPositionCoefficient * 10.0;

        TalonFXConfiguration motorConfiguration = new TalonFXConfiguration();
        motorConfiguration.slot0.kP = proportionalConstant;
        motorConfiguration.slot0.kI = integralConstant;
        motorConfiguration.slot0.kD = derivativeConstant;

        motorConfiguration.voltageCompSaturation = nominalVoltage;
        motorConfiguration.supplyCurrLimit.currentLimit = steerCurrentLimit;
        motorConfiguration.supplyCurrLimit.enable = true;

        TalonFX motor = new TalonFX(steerMotorPort);
        checkCtreError(motor.configAllSettings(motorConfiguration, CAN_TIMEOUT_MS), "Failed to configure Falcon 500 settings");

        motor.enableVoltageCompensation(true);
        checkCtreError(motor.configSelectedFeedbackSensor(TalonFXFeedbackDevice.IntegratedSensor, 0, CAN_TIMEOUT_MS), "Failed to set Falcon 500 feedback sensor");
        motor.setSensorPhase(true);
        motor.setInverted(moduleConfiguration.isSteerInverted() ? TalonFXInvertType.CounterClockwise : TalonFXInvertType.Clockwise);
        motor.setNeutralMode(NeutralMode.Brake);

        checkCtreError(motor.setSelectedSensorPosition(absoluteEncoder.getAbsoluteAngle() / sensorPositionCoefficient, 0, CAN_TIMEOUT_MS), "Failed to set Falcon 500 encoder position");

        // Reduce CAN status frame rates
        checkCtreError(
                motor.setStatusFramePeriod(
                        StatusFrameEnhanced.Status_1_General,
                        STATUS_FRAME_GENERAL_PERIOD_MS,
                        CAN_TIMEOUT_MS
                ),
                "Failed to configure Falcon status frame period"
        );

        return new Falcon500SteerController(motor,
                sensorPositionCoefficient,
                sensorVelocityCoefficient,
                TalonFXControlMode.Position,
                absoluteEncoder);
    }

    AbsoluteEncoder createAbsoluteEncoder(int steerEncoderPort, double steerOffset) {

        Direction direction = Direction.COUNTER_CLOCKWISE;
        CANCoderConfiguration config = new CANCoderConfiguration();
        config.absoluteSensorRange = AbsoluteSensorRange.Unsigned_0_to_360;
        config.magnetOffsetDegrees = Math.toDegrees(steerOffset);
        config.sensorDirection = direction == Direction.CLOCKWISE;

        CANCoder encoder = new CANCoder(steerEncoderPort);
        checkCtreError(encoder.configAllSettings(config, 250), "Failed to configure CANCoder");

        checkCtreError(encoder.setStatusFramePeriod(CANCoderStatusFrame.SensorData, canCoderPeriodMilliseconds, 250), "Failed to configure CANCoder update rate");

        AbsoluteEncoder absoluteEncoder = new CANCoderAbsoluteEncoder(encoder);
        return absoluteEncoder;
    }


}
