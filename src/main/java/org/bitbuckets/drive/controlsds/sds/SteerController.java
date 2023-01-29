package org.bitbuckets.drive.controlsds.sds;

import org.bitbuckets.lib.hardware.IAbsoluteEncoder;
import org.bitbuckets.lib.hardware.IMotorController;

public class SteerController implements ISteerController {
    private static final int ENCODER_RESET_ITERATIONS = 500;
    private static final double ENCODER_RESET_MAX_ANGULAR_VELOCITY = Math.toRadians(0.5);

    final IMotorController motor;
    final IAbsoluteEncoder encoder;

    final double sensorPositionCoefficient;

    private double referenceAngleRadians = 0.0;

    private double resetIteration = 0;

    public SteerController(IMotorController motor,
                           IAbsoluteEncoder encoder, double sensorPositionCoefficient) {
        this.motor = motor;
        this.encoder = encoder;
        this.sensorPositionCoefficient = sensorPositionCoefficient;
    }

    @Override
    public double getReferenceAngle() {
        return referenceAngleRadians;
    }

    @Override
    public void setReferenceAngle(double referenceAngleRadians) {
        double currentAngleRadians = motor.getPositionRaw() * sensorPositionCoefficient;

        // Reset the NEO's encoder periodically when the module is not rotating.
        // Sometimes (~5% of the time) when we initialize, the absolute encoder isn't fully set up, and we don't
        // end up getting a good reading. If we reset periodically this won't matter anymore.
        if (motor.getVelocityRaw() * (sensorPositionCoefficient * 10) < ENCODER_RESET_MAX_ANGULAR_VELOCITY) {
            if (++resetIteration >= ENCODER_RESET_ITERATIONS) {
                resetIteration = 0;
                double absoluteAngle = encoder.getAbsoluteAngle();
                motor.forceOffset(absoluteAngle / sensorPositionCoefficient);
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

        motor.moveToPosition(adjustedReferenceAngleRadians / sensorPositionCoefficient);


        this.referenceAngleRadians = referenceAngleRadians;
    }

    @Override
    public double getStateAngle() {
        double motorAngleRadians = motor.getPositionRaw() * sensorPositionCoefficient;
        motorAngleRadians %= 2.0 * Math.PI;
        if (motorAngleRadians < 0.0) {
            motorAngleRadians += 2.0 * Math.PI;
        }

        return motorAngleRadians;
    }

    @Override
    public double getAbsoluteAngle() {
        return encoder.getAbsoluteAngle();
    }

    @Override
    public void forceOffset(double position) {
        motor.forceOffset(position);
    }
}
