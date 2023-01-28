package org.bitbuckets.drive.controlsds.falcon;

import com.ctre.phoenix.motorcontrol.TalonFXControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import org.bitbuckets.drive.controlsds.sds.AbsoluteEncoder;
import org.bitbuckets.drive.controlsds.sds.SteerController;

@Deprecated
public class Falcon500SteerController implements SteerController {
    private static final int ENCODER_RESET_ITERATIONS = 500;
    private static final double ENCODER_RESET_MAX_ANGULAR_VELOCITY = Math.toRadians(0.5);

    private final TalonFX motor;
    private final double motorEncoderPositionCoefficient;
    private final double motorEncoderVelocityCoefficient;
    private final TalonFXControlMode motorControlMode;
    private final AbsoluteEncoder absoluteEncoder;

    private double referenceAngleRadians = 0.0;

    private double resetIteration = 0;

    public Falcon500SteerController(TalonFX motor,
                                    double motorEncoderPositionCoefficient,
                                    double motorEncoderVelocityCoefficient,
                                    TalonFXControlMode motorControlMode,
                                    AbsoluteEncoder absoluteEncoder) {
        this.motor = motor;
        this.motorEncoderPositionCoefficient = motorEncoderPositionCoefficient;
        this.motorEncoderVelocityCoefficient = motorEncoderVelocityCoefficient;
        this.motorControlMode = motorControlMode;
        this.absoluteEncoder = absoluteEncoder;
    }

    @Override
    public double getReferenceAngle() {
        return referenceAngleRadians;
    }

    @Override
    public void setReferenceAngle(double referenceAngleRadians) {
        double currentAngleRadians = motor.getSelectedSensorPosition() * motorEncoderPositionCoefficient;

        // Reset the NEO's encoder periodically when the module is not rotating.
        // Sometimes (~5% of the time) when we initialize, the absolute encoder isn't fully set up, and we don't
        // end up getting a good reading. If we reset periodically this won't matter anymore.
        if (motor.getSelectedSensorVelocity() * motorEncoderVelocityCoefficient < ENCODER_RESET_MAX_ANGULAR_VELOCITY) {
            if (++resetIteration >= ENCODER_RESET_ITERATIONS) {
                resetIteration = 0;
                double absoluteAngle = absoluteEncoder.getAbsoluteAngle();
                motor.setSelectedSensorPosition(absoluteAngle / motorEncoderPositionCoefficient);
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

        motor.set(motorControlMode, adjustedReferenceAngleRadians / motorEncoderPositionCoefficient);


        this.referenceAngleRadians = referenceAngleRadians;
    }

    @Override
    public double getStateAngle() {
        double motorAngleRadians = motor.getSelectedSensorPosition() * motorEncoderPositionCoefficient;
        motorAngleRadians %= 2.0 * Math.PI;
        if (motorAngleRadians < 0.0) {
            motorAngleRadians += 2.0 * Math.PI;
        }

        return motorAngleRadians;
    }
}
