package org.bitbuckets.lib.vendor.spark;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.SparkMaxPIDController;
import org.bitbuckets.drive.controlsds.sds.SwerveModuleConfiguration;
import org.bitbuckets.lib.IProcess;
import org.bitbuckets.lib.control.PIDConfig;
import org.bitbuckets.lib.hardware.IMotorController;
import org.bitbuckets.lib.hardware.MotorConfig;

import java.util.Optional;

import static org.bitbuckets.lib.vendor.spark.RevUtils.checkNeoError;

/**
 * Setup for a neo DriveController motor. This is only for the drive to preserve config values from SDS
 */
public class SparkSteerMotorSetup extends SparkSetup {

    final SwerveModuleConfiguration swerveModuleConfiguration;


    public SparkSteerMotorSetup(int canId, MotorConfig motorConfig, PIDConfig pidConfig, SwerveModuleConfiguration swerveModuleConfiguration) {
        super(canId, motorConfig, pidConfig, Optional.empty());
        this.swerveModuleConfiguration = swerveModuleConfiguration;
    }

    @Override
    public IMotorController build(IProcess self) {

        var motorController = super.build(self);

        // configure the raw motor with the same settings for SDS
        CANSparkMax motor = motorController.rawAccess(CANSparkMax.class);

        checkNeoError(motor.setPeriodicFramePeriod(CANSparkMaxLowLevel.PeriodicFrame.kStatus0, 100), "Failed to set periodic status frame 0 rate");
        checkNeoError(motor.setPeriodicFramePeriod(CANSparkMaxLowLevel.PeriodicFrame.kStatus1, 20), "Failed to set periodic status frame 1 rate");
        checkNeoError(motor.setPeriodicFramePeriod(CANSparkMaxLowLevel.PeriodicFrame.kStatus2, 20), "Failed to set periodic status frame 2 rate");
        checkNeoError(motor.setIdleMode(CANSparkMax.IdleMode.kBrake), "Failed to set NEO idle mode");
        motor.setInverted(!swerveModuleConfiguration.isSteerInverted());

        RelativeEncoder integratedEncoder = motor.getEncoder();
        checkNeoError(integratedEncoder.setPositionConversionFactor(2.0 * Math.PI * swerveModuleConfiguration.getSteerReduction()), "Failed to set NEO encoder conversion factor");
        checkNeoError(integratedEncoder.setVelocityConversionFactor(2.0 * Math.PI * swerveModuleConfiguration.getSteerReduction() / 60.0), "Failed to set NEO encoder conversion factor");

        SparkMaxPIDController controller = motor.getPIDController();
        // PID is set as tuneable by SparkSetup
//        checkNeoError(controller.setP(pidProportional), "Failed to set NEO PID proportional constant");
//        checkNeoError(controller.setI(pidIntegral), "Failed to set NEO PID integral constant");
//        checkNeoError(controller.setD(pidDerivative), "Failed to set NEO PID derivative constant");
        checkNeoError(controller.setFeedbackDevice(integratedEncoder), "Failed to set NEO PID feedback device");


        return motorController;

    }

}
