package org.bitbuckets.drive.controlsds.sds;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import org.bitbuckets.lib.log.ILoggable;

public class SwerveModule implements ISwerveModule, Runnable {
    private final IDriveController driveController;
    private final ISteerController steerController;

    final ILoggable<double[]> swerveAngleVoltage;
    private double steerAngle;
    private double driveVoltage;

    public SwerveModule(IDriveController driveController, ISteerController steerController, ILoggable<double[]> swerveAngleVelocity) {
        this.driveController = driveController;
        this.steerController = steerController;
        this.swerveAngleVoltage = swerveAngleVelocity;
    }


    @Override
    public SwerveModulePosition getPosition() {
        return new SwerveModulePosition(driveController.getStatePosition_meters(), Rotation2d.fromRadians(getSteerAngle()));
    }

    @Override
    public SwerveModuleState getState() {
        return new SwerveModuleState(getDriveVelocity(), Rotation2d.fromRadians(getSteerAngle()));
    }


    @Override
    public double getDriveVelocity() {
        return driveController.getStateVelocity();
    }

    @Override
    public double getSteerAngle() {
        return steerController.getStateAngle();
    }

    @Override
    public void set(double driveVoltage, double steerAngle) {

        steerAngle %= (2.0 * Math.PI);
        if (steerAngle < 0.0) {
            steerAngle += 2.0 * Math.PI;
        }

        double difference = steerAngle - getSteerAngle();
        // Change the target angle so the difference is in the range [-pi, pi) instead of [0, 2pi)
        if (difference >= Math.PI) {
            steerAngle -= 2.0 * Math.PI;
        } else if (difference < -Math.PI) {
            steerAngle += 2.0 * Math.PI;
        }
        difference = steerAngle - getSteerAngle(); // Recalculate difference

        // If the difference is greater than 90 deg or less than -90 deg the drive can be inverted so the total
        // movement of the module is less than 90 deg
        if (difference > Math.PI / 2.0 || difference < -Math.PI / 2.0) {
            // Only need to add 180 deg here because the target angle will be put back into the range [0, 2pi)
            steerAngle += Math.PI;
            driveVoltage *= -1.0;
        }

        // Put the target angle back into the range [0, 2pi)
        steerAngle %= (2.0 * Math.PI);
        if (steerAngle < 0.0) {
            steerAngle += 2.0 * Math.PI;
        }

        driveController.setReferenceVoltage(driveVoltage);
        steerController.setReferenceAngle(steerAngle);

        this.steerAngle = steerAngle;
        this.driveVoltage = driveVoltage;
    }

    @Override
    public void run() {
        swerveAngleVoltage.log(new double[]{
                Math.toDegrees(steerAngle),
                driveVoltage,
        });
    }
}