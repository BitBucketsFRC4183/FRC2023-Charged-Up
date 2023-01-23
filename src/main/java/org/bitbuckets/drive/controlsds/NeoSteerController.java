package org.bitbuckets.drive.controlsds;

import com.revrobotics.CANSparkMax;

public class NeoSteerController implements SteerController {
    private static final int ENCODER_RESET_ITERATIONS = 500;
    private static final double ENCODER_RESET_MAX_ANGULAR_VELOCITY = Math.toRadians(0.5);

    private final CANSparkMax motor;
    private final ThriftyEncoder encoder;
    //    private final SparkMaxAnalogSensor encoder;
    private final CANSparkMax.ControlType motorControlType;

    private double referenceAngleRadians = 0.0;

    private double resetIteration = 0;

    NeoSteerController(CANSparkMax motor,
                       CANSparkMax.ControlType motorControlType, ThriftyEncoder encoder) {
        this.motor = motor;
        this.motorControlType = motorControlType;
        this.encoder = encoder;
    }

    @Override
    public double getReferenceAngle() {
        return referenceAngleRadians;
    }

    @Override
    public void setReferenceAngle(double referenceAngleRadians) {
        double currentAngleRadians = motor.getEncoder().getPosition();

        // Reset the NEO's encoder periodically when the module is not rotating.
        // Sometimes (~5% of the time) when we initialize, the absolute encoder isn't fully set up, and we don't
        // end up getting a good reading. If we reset periodically this won't matter anymore.
        if (motor.getEncoder().getVelocity() < ENCODER_RESET_MAX_ANGULAR_VELOCITY) {
            if (++resetIteration >= ENCODER_RESET_ITERATIONS) {
                resetIteration = 0;
                double absoluteAngle = encoder.getAbsoluteAngle();
                motor.getEncoder().setPosition(absoluteAngle);
                currentAngleRadians = absoluteAngle;
            }
        } else {
            resetIteration = 0;
        }

        double currentAngleRadiansMod = currentAngleRadians % (2.0 * Math.PI);
        if (currentAngleRadiansMod < 0.0) {
            currentAngleRadiansMod += 2.0 * Math.PI;
        }

        // The reference angle has the range [0, 2pi) but the Falcon's encoder can go above that
        double adjustedReferenceAngleRadians = referenceAngleRadians + currentAngleRadians - currentAngleRadiansMod;
        if (referenceAngleRadians - currentAngleRadiansMod > Math.PI) {
            adjustedReferenceAngleRadians -= 2.0 * Math.PI;
        } else if (referenceAngleRadians - currentAngleRadiansMod < -Math.PI) {
            adjustedReferenceAngleRadians += 2.0 * Math.PI;
        }

        motor.getPIDController().setReference(adjustedReferenceAngleRadians, motorControlType.kPosition);


        this.referenceAngleRadians = referenceAngleRadians;
    }

    @Override
    public double getStateAngle() {
        double motorAngleRadians = encoder.getPositionRadians();
        motorAngleRadians %= 2.0 * Math.PI;
        if (motorAngleRadians < 0.0) {
            motorAngleRadians += 2.0 * Math.PI;
        }

        return motorAngleRadians;
    }
}
