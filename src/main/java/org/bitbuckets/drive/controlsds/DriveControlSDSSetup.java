package org.bitbuckets.drive.controlsds;

import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.motorcontrol.*;
import com.ctre.phoenix.motorcontrol.can.TalonFXConfiguration;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;
import com.ctre.phoenix.sensors.*;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.trajectory.TrapezoidProfile;
import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInLayouts;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardLayout;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.bitbuckets.bootstrap.Robot;
import org.bitbuckets.drive.DriveSDSConstants;
import org.bitbuckets.drive.controlsds.falcon.Falcon500IDriveController;
import org.bitbuckets.drive.controlsds.falcon.Falcon500ISteerController;
import org.bitbuckets.drive.controlsds.sds.*;
import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.ProcessPath;
import org.bitbuckets.lib.control.ProfiledPIDFController;
import org.bitbuckets.lib.hardware.IAbsoluteEncoder;
import org.bitbuckets.lib.log.DataLogger;
import org.bitbuckets.lib.sim.CTREPhysicsSim;

import static org.bitbuckets.lib.vendor.ctre.CtreUtils.checkCtreError;

/**
 * Sets up prereqs for a drive controller
 * <p>
 * really simple because a drivecontrol is super simple LMAO
 */
public class DriveControlSDSSetup implements ISetup<DriveControlSDS> {

    @Override
    public DriveControlSDS build(ProcessPath path) {
        DataLogger<DriveControlSDSDataAutoGen> logger = path.generatePushDataLogger(DriveControlSDSDataAutoGen::new);

        //holy fuck what are you DOING

        if (!Preferences.containsKey(DriveSDSConstants.kOrientPKey)) {
            Preferences.setDouble(DriveSDSConstants.kOrientPKey, DriveSDSConstants.kOrientkP);
        }
        if (!Preferences.containsKey(DriveSDSConstants.kOrientIKey)) {
            Preferences.setDouble(DriveSDSConstants.kOrientIKey, DriveSDSConstants.kOrientkI);
        }
        if (!Preferences.containsKey(DriveSDSConstants.kOrientDKey)) {
            Preferences.setDouble(DriveSDSConstants.kOrientDKey, DriveSDSConstants.kOrientkD);
        }
        if (!Preferences.containsKey(DriveSDSConstants.kBalancePKey)) {
            Preferences.setDouble(DriveSDSConstants.kBalancePKey, DriveSDSConstants.kBalancekP);
        }
        if (!Preferences.containsKey(DriveSDSConstants.kBalanceIKey)) {
            Preferences.setDouble(DriveSDSConstants.kBalanceIKey, DriveSDSConstants.kBalancekI);
        }
        if (!Preferences.containsKey(DriveSDSConstants.kBalanceDKey)) {
            Preferences.setDouble(DriveSDSConstants.kBalanceDKey, DriveSDSConstants.kBalancekD);
        }
        if (!Preferences.containsKey(DriveSDSConstants.autoBalanceDeadbandDegKey)) {
            Preferences.setDouble(DriveSDSConstants.autoBalanceDeadbandDegKey, DriveSDSConstants.BalanceDeadbandDeg);
        }


        if (!Preferences.containsKey(DriveSDSConstants.kDriveFeedForwardAKey)) {
            Preferences.setDouble(DriveSDSConstants.kDriveFeedForwardAKey, DriveSDSConstants.kDriveFeedForwardA);
        }
        if (!Preferences.containsKey(DriveSDSConstants.kDriveFeedForwardSKey)) {
            Preferences.setDouble(DriveSDSConstants.kDriveFeedForwardSKey, DriveSDSConstants.kDriveFeedForwardS);
        }
        if (!Preferences.containsKey(DriveSDSConstants.kDriveFeedForwardVKey)) {
            Preferences.setDouble(DriveSDSConstants.kDriveFeedForwardVKey, DriveSDSConstants.kDriveFeedForwardV);
        }

        if (!Preferences.containsKey(DriveSDSConstants.kOrientFKey)) {
            Preferences.setDouble(DriveSDSConstants.kOrientFKey, DriveSDSConstants.kOrientkF);
        }

        double wheelWearFactor = 1;

        double maxVelocity_metersPerSecond =
                6380.0 /
                        60.0 *
                        DriveSDSConstants.MK4_L2.getDriveReduction() *
                        (DriveSDSConstants.MK4_L2.getWheelDiameter() * wheelWearFactor) *
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
        ISwerveModule moduleFrontLeft = createSwerveModule(
                //Smart Dashboard Tab
                tab.getLayout("Front Left Module", BuiltInLayouts.kList).withSize(2, 4).withPosition(0, 0),
                DriveSDSConstants.frontLeftModuleDriveMotor_ID, //Drive Motor
                DriveSDSConstants.frontLeftModuleSteerMotor_ID, //Steer Motor
                DriveSDSConstants.frontLeftModuleSteerEncoder_CHANNEL, //Steer Encoder
                DriveSDSConstants.frontLeftModuleSteerOffset //Steer Offset
        );

        ISwerveModule moduleFrontRight = createSwerveModule(
                //Smart Dashboard Tab
                tab.getLayout("Front Right Module", BuiltInLayouts.kList).withSize(2, 4).withPosition(2, 0),
                DriveSDSConstants.frontRightModuleDriveMotor_ID, //Drive Motor
                DriveSDSConstants.frontRightModuleSteerMotor_ID, //Steer Motor
                DriveSDSConstants.frontRightModuleSteerEncoder_CHANNEL, //Steer Encoder
                DriveSDSConstants.frontRightModuleSteerOffset //Steer Offset
        );

        ISwerveModule moduleBackLeft = createSwerveModule(
                //Smart Dashboard Tab
                tab.getLayout("Back Left Module", BuiltInLayouts.kList).withSize(2, 4).withPosition(4, 0),
                DriveSDSConstants.backLeftModuleDriveMotor_ID, //Drive Motor
                DriveSDSConstants.backLeftModuleSteerMotor_ID, //Steer Motor
                DriveSDSConstants.backLeftModuleSteerEncoder_CHANNEL, //Steer Encoder
                DriveSDSConstants.backLeftModuleSteerOffset //Steer Offset
        );

        ISwerveModule moduleBackRight = createSwerveModule(
                //Smart Dashboard Tab
                tab.getLayout("Back Right Module", BuiltInLayouts.kList).withSize(2, 4).withPosition(6, 0),
                DriveSDSConstants.backRightModuleDriveMotor_ID, //Drive Motor
                DriveSDSConstants.backRightModuleSteerMotor_ID, //Steer Motor
                DriveSDSConstants.backRightModuleSteerEncoder_CHANNEL, //Steer Encoder
                DriveSDSConstants.backRightModuleSteerOffset //Steer Offset
        );

        //Calibrate the gyro only once when the drive subsystem is first initialized
        gyro.calibrate();

        double BalanceKP = Preferences.getDouble(DriveSDSConstants.kBalancePKey, DriveSDSConstants.kBalancekP);
        double BalanceKI = Preferences.getDouble(DriveSDSConstants.kBalanceIKey, DriveSDSConstants.kBalancekI);
        double BalanceKD = Preferences.getDouble(DriveSDSConstants.kBalanceDKey, DriveSDSConstants.kBalancekD);
        double OrientKP = Preferences.getDouble(DriveSDSConstants.kOrientPKey, DriveSDSConstants.kOrientkP);
        double OrientKI = Preferences.getDouble(DriveSDSConstants.kOrientIKey, DriveSDSConstants.kOrientkI);
        double OrientKD = Preferences.getDouble(DriveSDSConstants.kOrientDKey, DriveSDSConstants.kOrientkD);
        double OrientKF = Preferences.getDouble(DriveSDSConstants.kOrientFKey, DriveSDSConstants.kOrientkF);

        ProfiledPIDFController rotControllerRad = new ProfiledPIDFController(OrientKP, OrientKI, OrientKD, OrientKF, new TrapezoidProfile.Constraints(DriveSDSConstants.MAX_ANG_VELOCITY, DriveSDSConstants.MAX_ANG_VELOCITY));

        PIDController balanceController = new PIDController(BalanceKP, BalanceKI, BalanceKD);


        DriveControlSDS control = new DriveControlSDS(logger,
                moduleFrontLeft, moduleFrontRight, moduleBackLeft, moduleBackRight, kinematics);


        path.registerLoop(control::guaranteedLoggingLoop, "logging");

        return control;
    }

    ISwerveModule createSwerveModule(
            ShuffleboardLayout container,
            int driveMotorPort,
            int steerMotorPort,
            int steerEncoderPort,
            double steerOffset
    ) {
        var driveController = createDriveController(driveMotorPort, DriveSDSConstants.MK4_L2);
        var steerController = createSteerController(steerMotorPort, steerEncoderPort, steerOffset, DriveSDSConstants.MK4_L2);
        return new SwerveModule(null, driveController, steerController);
    }

    IDriveController createDriveController(int driveMotorPort, SwerveModuleConfiguration swerveModuleConfiguration) {
        TalonFXConfiguration motorConfiguration = new TalonFXConfiguration();

        double sensorPositionCoefficient = Math.PI * swerveModuleConfiguration.getWheelDiameter() * swerveModuleConfiguration.getDriveReduction() / DriveSDSConstants.TICKS_PER_ROTATION;
        double sensorVelocityCoefficient = sensorPositionCoefficient * 10.0;

        motorConfiguration.voltageCompSaturation = DriveSDSConstants.nominalVoltage;

        motorConfiguration.supplyCurrLimit.currentLimit = DriveSDSConstants.driveCurrentLimit;
        motorConfiguration.supplyCurrLimit.enable = true;

        var motor = new WPI_TalonFX(driveMotorPort);
        checkCtreError(motor.configAllSettings(motorConfiguration), "Failed to configure Falcon 500");

        // Enable voltage compensation
        motor.enableVoltageCompensation(true);

        motor.setNeutralMode(NeutralMode.Brake);

        motor.setInverted(swerveModuleConfiguration.isDriveInverted() ? TalonFXInvertType.Clockwise : TalonFXInvertType.CounterClockwise);
        motor.setSensorPhase(true);

        // Reduce CAN status frame rates
        checkCtreError(
                motor.setStatusFramePeriod(
                        StatusFrameEnhanced.Status_1_General,
                        DriveSDSConstants.STATUS_FRAME_GENERAL_PERIOD_MS,
                        DriveSDSConstants.CAN_TIMEOUT_MS
                ),
                "Failed to configure Falcon status frame period"
        );

        if (Robot.isSimulation()) {
            CTREPhysicsSim.getInstance().addTalonFX(motor, .5, 6800);
        }

        return new Falcon500IDriveController(motor, sensorVelocityCoefficient, DriveSDSConstants.nominalVoltage);
    }

    ISteerController createSteerController(int steerMotorPort, int steerEncoderPort, double steerOffset, SwerveModuleConfiguration swerveModuleConfiguration) {

        IAbsoluteEncoder absoluteEncoder = createAbsoluteEncoder(steerEncoderPort, steerOffset);

        final double sensorPositionCoefficient = 2.0 * Math.PI / DriveSDSConstants.TICKS_PER_ROTATION * swerveModuleConfiguration.getSteerReduction();
        final double sensorVelocityCoefficient = sensorPositionCoefficient * 10.0;

        TalonFXConfiguration motorConfiguration = new TalonFXConfiguration();
        motorConfiguration.slot0.kP = DriveSDSConstants.proportionalConstant;
        motorConfiguration.slot0.kI = DriveSDSConstants.integralConstant;
        motorConfiguration.slot0.kD = DriveSDSConstants.derivativeConstant;

        motorConfiguration.voltageCompSaturation = DriveSDSConstants.nominalVoltage;
        motorConfiguration.supplyCurrLimit.currentLimit = DriveSDSConstants.steerCurrentLimit;
        motorConfiguration.supplyCurrLimit.enable = true;

        var motor = new WPI_TalonFX(steerMotorPort);
        checkCtreError(motor.configAllSettings(motorConfiguration, DriveSDSConstants.CAN_TIMEOUT_MS), "Failed to configure Falcon 500 settings");

        motor.enableVoltageCompensation(true);
        checkCtreError(motor.configSelectedFeedbackSensor(TalonFXFeedbackDevice.IntegratedSensor, 0, DriveSDSConstants.CAN_TIMEOUT_MS), "Failed to set Falcon 500 feedback sensor");
        motor.setSensorPhase(true);
        motor.setInverted(swerveModuleConfiguration.isSteerInverted() ? TalonFXInvertType.CounterClockwise : TalonFXInvertType.Clockwise);
        motor.setNeutralMode(NeutralMode.Brake);

        checkCtreError(motor.setSelectedSensorPosition(absoluteEncoder.getAbsoluteAngle() / sensorPositionCoefficient, 0, DriveSDSConstants.CAN_TIMEOUT_MS), "Failed to set Falcon 500 encoder position");

        // Reduce CAN status frame rates
        checkCtreError(
                motor.setStatusFramePeriod(
                        StatusFrameEnhanced.Status_1_General,
                        DriveSDSConstants.STATUS_FRAME_GENERAL_PERIOD_MS,
                        DriveSDSConstants.CAN_TIMEOUT_MS
                ),
                "Failed to configure Falcon status frame period"
        );

        if (Robot.isSimulation()) {
            CTREPhysicsSim.getInstance().addTalonFX(motor, .5, 6800);
        }

        return new Falcon500ISteerController(motor,
                sensorPositionCoefficient,
                sensorVelocityCoefficient,
                TalonFXControlMode.Position,
                absoluteEncoder);
    }

    IAbsoluteEncoder createAbsoluteEncoder(int steerEncoderPort, double steerOffset) {

        Direction direction = Direction.COUNTER_CLOCKWISE;
        CANCoderConfiguration config = new CANCoderConfiguration();
        config.absoluteSensorRange = AbsoluteSensorRange.Unsigned_0_to_360;
        config.magnetOffsetDegrees = Math.toDegrees(steerOffset);
        config.sensorDirection = direction == Direction.CLOCKWISE;
        var encoder = new WPI_CANCoder(steerEncoderPort);
        checkCtreError(encoder.configAllSettings(config, 250), "Failed to configure CANCoder");
        waitForCanCoder(encoder);

        checkCtreError(encoder.setStatusFramePeriod(CANCoderStatusFrame.SensorData, DriveSDSConstants.canCoderPeriodMilliseconds, 250), "Failed to configure CANCoder update rate");

        IAbsoluteEncoder absoluteEncoder = new CANCoderAbsoluteEncoder(encoder);
        return absoluteEncoder;
    }

    private static void waitForCanCoder(WPI_CANCoder canCoder) {
        /*
         * Wait for up to 1000 ms for a good CANcoder signal.
         *
         * This prevents a race condition during program startup
         * where we try to synchronize the Falcon encoder to the
         * CANcoder before we have received any position signal
         * from the CANcoder.
         */
        int initTime = 0;

        ErrorCode shm = canCoder.getLastError();
        for (int i = 0; i < 100; ++i) {
            canCoder.getAbsolutePosition();

            shm = canCoder.getLastError();
            if (shm.equals(ErrorCode.OK)) {
//                DriverStation.reportWarning("init took: " + initTime, false);
                break;
            }
            Timer.delay(0.1);
            initTime += 10;
        }

        System.out.println("how many tume rune " + initTime);


//        DriverStation.reportWarning("BAD BAD BAD BAD BAD BAD B" + shm, false);
    }

}
