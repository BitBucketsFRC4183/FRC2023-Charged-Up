package org.bitbuckets.drive.controlsds.neo;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel;
import com.revrobotics.RelativeEncoder;
import org.bitbuckets.drive.DriveSDSConstants;
import org.bitbuckets.drive.controlsds.sds.IDriveController;
import org.bitbuckets.drive.controlsds.sds.SwerveModuleConfiguration;
import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.ProcessPath;
import org.bitbuckets.lib.hardware.IMotorController;
import org.bitbuckets.lib.vendor.spark.SparkSetup;

import static org.bitbuckets.lib.vendor.spark.RevUtils.checkNeoError;

public class NeoDriveControllerSetup implements ISetup<IDriveController> {

    final ISetup<IMotorController> motor;
    final SwerveModuleConfiguration swerveModuleConfiguration;
    double nominalVoltage = 12;
    double currentLimit = 80;

    public NeoDriveControllerSetup(SparkSetup motor, SwerveModuleConfiguration swerveModuleConfiguration) {
        this.motor = motor;
        this.swerveModuleConfiguration = swerveModuleConfiguration;
    }

    @Override
    public IDriveController build(ProcessPath path) {
        // build the motor controller passed in by the caller (Should be a SparkSetup)
        var motorController = this.motor.build(path.addChild("drive"));

        // configure the raw motor with the same settings for SDS
        // TODO: this neo init code was taken straight from the SDS library. It should be moved to SparkSetup after being tested on a live robot
        CANSparkMax neo = motorController.rawAccess(CANSparkMax.class);
        neo.setInverted(swerveModuleConfiguration.isDriveInverted());

        // Setup voltage compensation
        checkNeoError(neo.enableVoltageCompensation(nominalVoltage), "Failed to enable voltage compensation");

        checkNeoError(neo.setSmartCurrentLimit((int) currentLimit), "Failed to set current limit for NEO");

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

        // pass our configured motorController back to the DriveController
        return new NeoDriveController(motorController, DriveSDSConstants.nominalVoltage);
    }
}
