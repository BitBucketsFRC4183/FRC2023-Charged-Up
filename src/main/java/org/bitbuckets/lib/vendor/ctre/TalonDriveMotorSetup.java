package org.bitbuckets.lib.vendor.ctre;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.StatusFrameEnhanced;
import com.ctre.phoenix.motorcontrol.TalonFXInvertType;
import com.ctre.phoenix.motorcontrol.can.TalonFXConfiguration;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;
import org.bitbuckets.drive.controlsds.sds.SwerveModuleConfiguration;
import org.bitbuckets.lib.IProcess;
import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.hardware.IMotorController;

public class TalonDriveMotorSetup implements ISetup<IMotorController> {

    private static final double TICKS_PER_ROTATION = 2048.0;
    private static final int CAN_TIMEOUT_MS = 250;
    private static final int STATUS_FRAME_GENERAL_PERIOD_MS = 250;

    final int canId;
    final SwerveModuleConfiguration moduleConfiguration;

    double nominalVoltage = 12;
    double currentLimit = 80;

    public TalonDriveMotorSetup(int canId, SwerveModuleConfiguration moduleConfiguration) {
        this.canId = canId;
        this.moduleConfiguration = moduleConfiguration;
    }

    @Override
    public IMotorController build(IProcess self) {

        TalonFXConfiguration motorConfiguration = new TalonFXConfiguration();

        double sensorPositionCoefficient = Math.PI * moduleConfiguration.getWheelDiameter() * moduleConfiguration.getDriveReduction() / TICKS_PER_ROTATION;
        double sensorVelocityCoefficient = sensorPositionCoefficient * 10.0;

        motorConfiguration.voltageCompSaturation = nominalVoltage;

        motorConfiguration.supplyCurrLimit.currentLimit = currentLimit;
        motorConfiguration.supplyCurrLimit.enable = true;

        WPI_TalonFX motor = new WPI_TalonFX(canId);
        CtreUtils.checkCtreError(motor.configAllSettings(motorConfiguration), "Failed to configure Falcon 500");

        // Enable voltage compensation
        motor.enableVoltageCompensation(true);

        motor.setNeutralMode(NeutralMode.Brake);

        motor.setInverted(moduleConfiguration.isDriveInverted() ? TalonFXInvertType.Clockwise : TalonFXInvertType.CounterClockwise);
        motor.setSensorPhase(true);

        // Reduce CAN status frame rates
        CtreUtils.checkCtreError(
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
