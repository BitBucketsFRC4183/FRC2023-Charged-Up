package org.bitbuckets.lib.vendor.ctre;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.StatusFrameEnhanced;
import com.ctre.phoenix.motorcontrol.TalonFXFeedbackDevice;
import com.ctre.phoenix.motorcontrol.TalonFXInvertType;
import com.ctre.phoenix.motorcontrol.can.TalonFXConfiguration;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;
import org.bitbuckets.drive.controlsds.sds.SwerveModuleConfiguration;
import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.ProcessPath;
import org.bitbuckets.lib.hardware.IMotorController;

import static org.bitbuckets.lib.vendor.ctre.CtreUtils.checkCtreError;

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
    public IMotorController build(ProcessPath path) {

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
        checkCtreError(motor.configAllSettings(motorConfiguration, CAN_TIMEOUT_MS), "Failed to configure Falcon 500 settings");

        motor.enableVoltageCompensation(true);
        checkCtreError(motor.configSelectedFeedbackSensor(TalonFXFeedbackDevice.IntegratedSensor, 0, CAN_TIMEOUT_MS), "Failed to set Falcon 500 feedback sensor");
        motor.setSensorPhase(true);
        motor.setInverted(moduleConfiguration.isSteerInverted() ? TalonFXInvertType.CounterClockwise : TalonFXInvertType.Clockwise);
        motor.setNeutralMode(NeutralMode.Brake);

        // TODO: this differs between talons and neos
//        checkCtreError(motor.setSelectedSensorPosition(absoluteEncoder.getAbsoluteAngle() / sensorPositionCoefficient, 0, CAN_TIMEOUT_MS), "Failed to set Falcon 500 encoder position");

        // Reduce CAN status frame rates
        checkCtreError(
                motor.setStatusFramePeriod(
                        StatusFrameEnhanced.Status_1_General,
                        STATUS_FRAME_GENERAL_PERIOD_MS,
                        CAN_TIMEOUT_MS
                ),
                "Failed to configure Falcon status frame period"
        );

        return new TalonRelativeMotorController(motor);
    }
}
