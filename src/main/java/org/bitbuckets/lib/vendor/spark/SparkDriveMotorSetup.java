package org.bitbuckets.lib.vendor.spark;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel;
import com.revrobotics.RelativeEncoder;
import org.bitbuckets.drive.controlsds.sds.SwerveModuleConfiguration;
import org.bitbuckets.lib.ProcessPath;
import org.bitbuckets.lib.hardware.IMotorController;
import org.bitbuckets.lib.hardware.MotorConfig;

import static org.bitbuckets.lib.vendor.spark.RevUtils.checkNeoError;

/**
 * Setup for a neo DriveController motor. This is only for the drive to preserve config values from SDS
 */
public class SparkDriveMotorSetup extends SparkSetup {

    final SwerveModuleConfiguration swerveModuleConfiguration;

    double nominalVoltage = 12;

    public SparkDriveMotorSetup(int canId, MotorConfig motorConfig, SwerveModuleConfiguration swerveModuleConfiguration) {
        super(canId, motorConfig);
        this.swerveModuleConfiguration = swerveModuleConfiguration;
    }

    @Override
    public IMotorController build(ProcessPath path) {
        var motor = super.build(path);

        // configure the raw motor with the same settings for SDS
        CANSparkMax neo = motor.rawAccess(CANSparkMax.class);
        neo.setInverted(swerveModuleConfiguration.isDriveInverted());

        // Setup voltage compensation
        checkNeoError(neo.enableVoltageCompensation(nominalVoltage), "Failed to enable voltage compensation");

        checkNeoError(neo.setSmartCurrentLimit((int) swerveModuleConfiguration.getDriveCurrentLimit()), "Failed to set current limit for NEO");

        checkNeoError(neo.setPeriodicFramePeriod(CANSparkMaxLowLevel.PeriodicFrame.kStatus0, 100), "Failed to set periodic status frame 0 rate");
        checkNeoError(neo.setPeriodicFramePeriod(CANSparkMaxLowLevel.PeriodicFrame.kStatus1, 20), "Failed to set periodic status frame 1 rate");
        checkNeoError(neo.setPeriodicFramePeriod(CANSparkMaxLowLevel.PeriodicFrame.kStatus2, 20), "Failed to set periodic status frame 2 rate");

        // Set neutral mode to brake
        neo.setIdleMode(CANSparkMax.IdleMode.kBrake);

        // Setup encoder
        RelativeEncoder encoder = neo.getEncoder();
        double positionConversionFactor = Math.PI * swerveModuleConfiguration.getWheelDiameter() * swerveModuleConfiguration.getDriveReduction();
        encoder.setPositionConversionFactor(positionConversionFactor);
        encoder.setVelocityConversionFactor(positionConversionFactor / 60.0);

        return motor;

    }

}
