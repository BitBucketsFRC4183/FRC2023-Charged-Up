package org.bitbuckets.drive.controlsds.neo;

import com.ctre.phoenix.sensors.WPI_PigeonIMU;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.SparkMaxPIDController;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInLayouts;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardLayout;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.bitbuckets.drive.DriveSDSConstants;
import org.bitbuckets.drive.controlsds.DriveControlSDS;
import org.bitbuckets.drive.controlsds.DriveControlSDSDataAutoGen;
import org.bitbuckets.drive.controlsds.ThriftyEncoder;
import org.bitbuckets.drive.controlsds.sds.*;
import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.ProcessPath;
import org.bitbuckets.lib.log.DataLogger;

import static org.bitbuckets.drive.controlsds.sds.RevUtils.checkNeoError;

/**
 * Sets up prereqs for a drive controller
 * <p>
 */
public class NeoControlSDSSetup implements ISetup<DriveControlSDS> {

    @Override
    public DriveControlSDS build(ProcessPath path) {
        DataLogger<DriveControlSDSDataAutoGen> logger = path.generatePushDataLogger(DriveControlSDSDataAutoGen::new);

        double wheelWearFactor = 1;

        double maxVelocity_metersPerSecond = 60.0 *
                DriveSDSConstants.MK4I_L2.getDriveReduction() *
                (DriveSDSConstants.MK4I_L2.getWheelDiameter() * wheelWearFactor) *
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
        // By default, we will use Falcon 500s in standard configuration. But if you use
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

        DriveControlSDS control = new DriveControlSDS(logger,
                moduleFrontLeft,
                moduleFrontRight,
                moduleBackLeft, moduleBackRight, kinematics);


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
        var driveController = createDriveController(driveMotorPort, DriveSDSConstants.MK4I_L2);
        var steerController = createSteerController(steerMotorPort, steerEncoderPort, steerOffset, DriveSDSConstants.MK4I_L2);
        return new ModuleImplementation(driveController, steerController);
    }

    DriveController createDriveController(int driveMotorPort, ModuleConfiguration moduleConfiguration) {
        CANSparkMax motor = new CANSparkMax(driveMotorPort, CANSparkMaxLowLevel.MotorType.kBrushless);
        motor.setInverted(moduleConfiguration.isDriveInverted());

        // Setup voltage compensation
        checkNeoError(motor.enableVoltageCompensation(DriveSDSConstants.nominalVoltage), "Failed to enable voltage compensation");

        checkNeoError(motor.setSmartCurrentLimit((int) DriveSDSConstants.driveCurrentLimit), "Failed to set current limit for NEO");

        checkNeoError(motor.setPeriodicFramePeriod(CANSparkMaxLowLevel.PeriodicFrame.kStatus0, 100), "Failed to set periodic status frame 0 rate");
        checkNeoError(motor.setPeriodicFramePeriod(CANSparkMaxLowLevel.PeriodicFrame.kStatus1, 20), "Failed to set periodic status frame 1 rate");
        checkNeoError(motor.setPeriodicFramePeriod(CANSparkMaxLowLevel.PeriodicFrame.kStatus2, 20), "Failed to set periodic status frame 2 rate");
        // Set neutral mode to brake
        motor.setIdleMode(CANSparkMax.IdleMode.kBrake);

        // Setup encoder
        RelativeEncoder encoder = motor.getEncoder();
        double positionConversionFactor = Math.PI * moduleConfiguration.getWheelDiameter() * moduleConfiguration.getDriveReduction();
        encoder.setPositionConversionFactor(positionConversionFactor);
        encoder.setVelocityConversionFactor(positionConversionFactor / 60.0);
        return new NeoDriveController(motor, DriveSDSConstants.nominalVoltage);
    }

    SteerController createSteerController(int steerMotorPort, int steerEncoderPort, double steerOffset, ModuleConfiguration moduleConfiguration) {

        CANSparkMax motor = new CANSparkMax(steerMotorPort, CANSparkMaxLowLevel.MotorType.kBrushless);

//        SparkMaxAlternateEncoder alternateEncoder = motor.getAlternateEncoder(SparkMaxAlternateEncoder.Type.kQuadrature, );
//        AbsoluteEncoder absoluteEncoder = createAbsoluteEncoder(steerEncoderPort, steerOffset)
//        SparkMaxAnalogSensor analogEncoder = motor.getAnalog(SparkMaxAnalogSensor.Mode.kAbsolute);

        AnalogInput ai = new AnalogInput(steerEncoderPort);

        ThriftyEncoder absoluteEncoder = new ThriftyEncoder(ai);

        checkNeoError(motor.setPeriodicFramePeriod(CANSparkMaxLowLevel.PeriodicFrame.kStatus0, 100), "Failed to set periodic status frame 0 rate");
        checkNeoError(motor.setPeriodicFramePeriod(CANSparkMaxLowLevel.PeriodicFrame.kStatus1, 20), "Failed to set periodic status frame 1 rate");
        checkNeoError(motor.setPeriodicFramePeriod(CANSparkMaxLowLevel.PeriodicFrame.kStatus2, 20), "Failed to set periodic status frame 2 rate");
        checkNeoError(motor.setIdleMode(CANSparkMax.IdleMode.kBrake), "Failed to set NEO idle mode");
        motor.setInverted(!moduleConfiguration.isSteerInverted());

        checkNeoError(motor.enableVoltageCompensation(DriveSDSConstants.nominalVoltage), "Failed to enable voltage compensation");


        checkNeoError(motor.setSmartCurrentLimit((int) Math.round(DriveSDSConstants.steerCurrentLimit)), "Failed to set NEO current limits");


        SparkMaxPIDController controller = motor.getPIDController();

//        controller.setFeedbackDevice(absoluteEncoder);

        checkNeoError(controller.setP(DriveSDSConstants.proportionalConstant), "Failed to set NEO PID proportional constant");
        checkNeoError(controller.setI(DriveSDSConstants.integralConstant), "Failed to set NEO PID integral constant");
        checkNeoError(controller.setD(DriveSDSConstants.derivativeConstant), "Failed to set NEO PID derivative constant");

//        checkNeoError(controller.setFeedbackDevice(absoluteEncoder), "Failed to set NEO PID feedback device");

        return new NeoSteerController(motor,
                CANSparkMax.ControlType.kPosition,
                absoluteEncoder);
    }

//    AbsoluteEncoder createAbsoluteEncoder(int steerEncoderPort, double steerOffset) {
//
//        Direction direction = Direction.COUNTER_CLOCKWISE;
//        CANCoderConfiguration config = new CANCoderConfiguration();
//        config.absoluteSensorRange = AbsoluteSensorRange.Unsigned_0_to_360;
//        config.magnetOffsetDegrees = Math.toDegrees(steerOffset);
//        config.sensorDirection = direction == Direction.CLOCKWISE;
//
//        var encoder = new WPI_CANCoder(steerEncoderPort);
//        checkCtreError(encoder.configAllSettings(config, 250), "Failed to configure CANCoder");
//
//        checkCtreError(encoder.setStatusFramePeriod(CANCoderStatusFrame.SensorData, DriveSDSConstants.canCoderPeriodMilliseconds, 250), "Failed to configure CANCoder update rate");
//
//        AbsoluteEncoder absoluteEncoder = new CANCoderAbsoluteEncoder(encoder);
//        return absoluteEncoder;
//    }


}
