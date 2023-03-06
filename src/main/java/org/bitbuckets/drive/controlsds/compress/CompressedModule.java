package org.bitbuckets.drive.controlsds.compress;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import org.bitbuckets.drive.controlsds.sds.ISwerveModule;
import org.bitbuckets.drive.controlsds.sds.SteerController;
import org.bitbuckets.lib.core.HasLogLoop;
import org.bitbuckets.lib.hardware.IAbsoluteEncoder;
import org.bitbuckets.lib.hardware.IMotorController;

public class CompressedModule implements ISwerveModule, HasLogLoop {

    final IMotorController driveMotor;
    final IMotorController steerMotor;
    final IAbsoluteEncoder absoluteEncoder;
    final double sensorPositionCoefficient;

    public CompressedModule(IMotorController driveMotor, IMotorController steerMotor, IAbsoluteEncoder absoluteEncoder, double sensorPositionCoefficient) {
        this.driveMotor = driveMotor;
        this.steerMotor = steerMotor;
        this.absoluteEncoder = absoluteEncoder;
        this.sensorPositionCoefficient = sensorPositionCoefficient;
    }

    @Override
    public SwerveModulePosition getPosition() {
        return new SwerveModulePosition(
                driveMotor.getPositionMechanism_meters(),
                Rotation2d.fromRadians(absoluteEncoder.getAbsoluteAngle())
        );
    }

    @Override
    public SwerveModuleState getState() {
        return new SwerveModuleState(
                driveMotor.getVelocityMechanism_metersPerSecond(),
                Rotation2d.fromRadians(absoluteEncoder.getAbsoluteAngle())
        );
    }

    @Override
    public double getDriveVelocity() {
        return driveMotor.getVelocityMechanism_metersPerSecond();
    }

    @Override
    public double getSteerAngle() {
        return absoluteEncoder.getAbsoluteAngle();
    }

    @Override
    public void stopMotor() {
        driveMotor.moveAtVoltage(0);
        steerMotor.moveAtVoltage(0);
    }

    double referenceAngleRadians = 0;
    int resetIteration = 0;

    @Override
    public void set(double driveVoltage, double referenceAngleRadians) {

        //set turn

        referenceAngleRadians %= (2.0 * Math.PI);
        if (referenceAngleRadians < 0.0) {
            referenceAngleRadians += 2.0 * Math.PI;
        }

        double difference = referenceAngleRadians - getSteerAngle();
        // Change the target angle so the difference is in the range [-pi, pi) instead of [0, 2pi)
        if (difference >= Math.PI) {
            referenceAngleRadians -= 2.0 * Math.PI;
        } else if (difference < -Math.PI) {
            referenceAngleRadians += 2.0 * Math.PI;
        }
        difference = referenceAngleRadians - getSteerAngle(); // Recalculate difference

        // If the difference is greater than 90 deg or less than -90 deg the drive can be inverted so the total
        // movement of the module is less than 90 deg
        if (difference > Math.PI / 2.0 || difference < -Math.PI / 2.0) {
            // Only need to add 180 deg here because the target angle will be put back into the range [0, 2pi)
            referenceAngleRadians += Math.PI;
            driveVoltage *= -1.0;
        }

        // Put the target angle back into the range [0, 2pi)
        referenceAngleRadians %= (2.0 * Math.PI);
        if (referenceAngleRadians < 0.0) {
            referenceAngleRadians += 2.0 * Math.PI;
        }

        double currentAngleRadians = steerMotor.getPositionRaw() * sensorPositionCoefficient;

        // Reset the NEO's encoder periodically when the module is not rotating.
        // Sometimes (~5% of the time) when we initialize, the absolute encoder isn't fully set up, and we don't
        // end up getting a good reading. If we reset periodically this won't matter anymore.
        if (steerMotor.getVelocityRaw() * (sensorPositionCoefficient * 10) < Math.toRadians(0.5)) {
            if (++resetIteration >= 500) {
                resetIteration = 0;
                double absoluteAngle = absoluteEncoder.getAbsoluteAngle();
                steerMotor.forceOffset(absoluteAngle / sensorPositionCoefficient);
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

        steerMotor.moveToPosition(adjustedReferenceAngleRadians / sensorPositionCoefficient);

        //set drive

        driveMotor.moveAtPercent(driveVoltage / 12.0);


        this.referenceAngleRadians = referenceAngleRadians;
    }


    @Override
    public void logLoop() {

    }
}
