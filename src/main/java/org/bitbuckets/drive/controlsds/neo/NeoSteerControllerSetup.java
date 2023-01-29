package org.bitbuckets.drive.controlsds.neo;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.SparkMaxPIDController;
import org.bitbuckets.drive.controlsds.sds.ISteerController;
import org.bitbuckets.drive.controlsds.sds.SwerveModuleConfiguration;
import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.ProcessPath;
import org.bitbuckets.lib.hardware.IAbsoluteEncoder;
import org.bitbuckets.lib.hardware.IMotorController;
import org.bitbuckets.lib.vendor.spark.SparkSetup;

import static org.bitbuckets.lib.vendor.spark.RevUtils.checkNeoError;

public class NeoSteerControllerSetup implements ISetup<ISteerController> {

    final ISetup<IMotorController> motor;
    final ISetup<IAbsoluteEncoder> encoder;

    final SwerveModuleConfiguration swerveModuleConfiguration;

    double nominalVoltage = 12;
    double steerCurrentLimit = 20;
    double pidProportional = 1;
    double pidIntegral = 0;
    double pidDerivative = .1;

    public NeoSteerControllerSetup(SparkSetup motor, ISetup<IAbsoluteEncoder> encoder, SwerveModuleConfiguration swerveModuleConfiguration) {
        this.motor = motor;
        this.encoder = encoder;
        this.swerveModuleConfiguration = swerveModuleConfiguration;
    }

    @Override
    public ISteerController build(ProcessPath path) {

        // build the motor controller passed in by the caller (Should be a SparkSetup)
        var motorController = this.motor.build(path.addChild("drive"));
        var absoluteEncoder = this.encoder.build(path.addChild("steer-encoder"));

        // configure the raw motor with the same settings for SDS
        // TODO: this neo init code was taken straight from the SDS library. It should be moved to SparkSetup after being tested on a live robot
        CANSparkMax motor = motorController.rawAccess(CANSparkMax.class);

        checkNeoError(motor.setPeriodicFramePeriod(CANSparkMaxLowLevel.PeriodicFrame.kStatus0, 100), "Failed to set periodic status frame 0 rate");
        checkNeoError(motor.setPeriodicFramePeriod(CANSparkMaxLowLevel.PeriodicFrame.kStatus1, 20), "Failed to set periodic status frame 1 rate");
        checkNeoError(motor.setPeriodicFramePeriod(CANSparkMaxLowLevel.PeriodicFrame.kStatus2, 20), "Failed to set periodic status frame 2 rate");
        checkNeoError(motor.setIdleMode(CANSparkMax.IdleMode.kBrake), "Failed to set NEO idle mode");
        motor.setInverted(!swerveModuleConfiguration.isSteerInverted());
        checkNeoError(motor.enableVoltageCompensation(nominalVoltage), "Failed to enable voltage compensation");
        checkNeoError(motor.setSmartCurrentLimit((int) Math.round(steerCurrentLimit)), "Failed to set NEO current limits");

        RelativeEncoder integratedEncoder = motor.getEncoder();
        checkNeoError(integratedEncoder.setPositionConversionFactor(2.0 * Math.PI * swerveModuleConfiguration.getSteerReduction()), "Failed to set NEO encoder conversion factor");
        checkNeoError(integratedEncoder.setVelocityConversionFactor(2.0 * Math.PI * swerveModuleConfiguration.getSteerReduction() / 60.0), "Failed to set NEO encoder conversion factor");
//        checkNeoError(integratedEncoder.setPosition(absoluteEncoder.getAbsoluteAngle()), "Failed to set NEO encoder position");

        SparkMaxPIDController controller = motor.getPIDController();
        checkNeoError(controller.setP(pidProportional), "Failed to set NEO PID proportional constant");
        checkNeoError(controller.setI(pidIntegral), "Failed to set NEO PID integral constant");
        checkNeoError(controller.setD(pidDerivative), "Failed to set NEO PID derivative constant");
        checkNeoError(controller.setFeedbackDevice(integratedEncoder), "Failed to set NEO PID feedback device");


        return new NeoSteerController(
                motorController,
                absoluteEncoder
        );
    }
}
