package org.bitbuckets.lib.vendor.ctre;

import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.StatusFrameEnhanced;
import com.ctre.phoenix.motorcontrol.TalonFXFeedbackDevice;
import com.ctre.phoenix.motorcontrol.TalonFXInvertType;
import com.ctre.phoenix.motorcontrol.can.TalonFXConfiguration;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;
import edu.wpi.first.math.system.plant.DCMotor;
import edu.wpi.first.wpilibj.DriverStation;
import org.bitbuckets.drive.controlsds.sds.SwerveModuleConfiguration;
import org.bitbuckets.lib.ILogAs;
import org.bitbuckets.lib.IProcess;
import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.hardware.IMotorController;
import org.bitbuckets.lib.hardware.MotorConfig;
import org.bitbuckets.lib.hardware.OptimizationMode;

import java.util.Optional;

@Deprecated
public class TalonSteerMotorSetup implements ISetup<IMotorController> {

    private static final double TICKS_PER_ROTATION = 2048.0;
    private static final int CAN_TIMEOUT_MS = 250;
    private static final int STATUS_FRAME_GENERAL_PERIOD_MS = 250;

    final int canId;
    final SwerveModuleConfiguration moduleConfiguration;

    double nominalVoltage = 12;
    double currentLimit = 20;

    double proportionalConstant = .2;
    double integralConstant = 0;
    double derivativeConstant = .1;


    public TalonSteerMotorSetup(int canId, SwerveModuleConfiguration moduleConfiguration) {
        this.canId = canId;
        this.moduleConfiguration = moduleConfiguration;
    }

    @Override
    public IMotorController build(IProcess self) {

        final double sensorPositionCoefficient = 2.0 * Math.PI / TICKS_PER_ROTATION * moduleConfiguration.getSteerReduction();
        final double sensorVelocityCoefficient = sensorPositionCoefficient * 10.0;

        TalonFXConfiguration motorConfiguration = new TalonFXConfiguration();
        motorConfiguration.slot0.kP = proportionalConstant;
        motorConfiguration.slot0.kI = integralConstant;
        motorConfiguration.slot0.kD = derivativeConstant;
        motorConfiguration.voltageCompSaturation = nominalVoltage;
        motorConfiguration.supplyCurrLimit.currentLimit = currentLimit;
        motorConfiguration.supplyCurrLimit.enable = true;

        WPI_TalonFX motor = new WPI_TalonFX(canId);
        ErrorCode errorCode2 = motor.configAllSettings(motorConfiguration, CAN_TIMEOUT_MS);
        if (errorCode2 != ErrorCode.OK) {
            DriverStation.reportError(String.format("%s: %s", "Failed to configure Falcon 500 settings", errorCode2.toString()), false);
        }

        motor.enableVoltageCompensation(true);
        ErrorCode errorCode1 = motor.configSelectedFeedbackSensor(TalonFXFeedbackDevice.IntegratedSensor, 0, CAN_TIMEOUT_MS);
        if (errorCode1 != ErrorCode.OK) {
            DriverStation.reportError(String.format("%s: %s", "Failed to set Falcon 500 feedback sensor", errorCode1.toString()), false);
        }
        motor.setSensorPhase(true);
        motor.setInverted(moduleConfiguration.isSteerInverted() ? TalonFXInvertType.CounterClockwise : TalonFXInvertType.Clockwise);
        motor.setNeutralMode(NeutralMode.Brake);

        // Reduce CAN status frame rates
        ErrorCode errorCode = motor.setStatusFramePeriod(
                StatusFrameEnhanced.Status_1_General,
                STATUS_FRAME_GENERAL_PERIOD_MS,
                CAN_TIMEOUT_MS
        );
        if (errorCode != ErrorCode.OK) {
            DriverStation.reportError(String.format("%s: %s", "Failed to configure Falcon status frame period", errorCode.toString()), false);
        }

        var ctrl = new TalonRelativeMotorController(motor, new MotorConfig(
                sensorPositionCoefficient,
                10,
                moduleConfiguration.getWheelDiameter() * Math.PI,
                moduleConfiguration.isSteerInverted(),
                true,
                moduleConfiguration.getSteerCurrentLimit(),
                Optional.empty(),
                Optional.empty(),
                false,
                false,
                false, OptimizationMode.GENERIC,
                DCMotor.getFalcon500(1),
                false
        ));

        TalonLogger logger = new TalonLogger(
                ctrl,
                self.generateLogger(ILogAs.DOUBLE, "pos-setpoint-mechanism-rotations"),
                self.generateLogger(ILogAs.DOUBLE, "encoder-mechanism-rotations"),
                self.generateLogger(ILogAs.DOUBLE, "encoder-position-raw")
        );

        self.registerLogLoop(logger);
        return ctrl;
    }
}
