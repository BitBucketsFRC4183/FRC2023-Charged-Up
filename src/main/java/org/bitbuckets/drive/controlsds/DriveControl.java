package org.bitbuckets.drive.controlsds;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import org.bitbuckets.drive.DriveConstants;
import org.bitbuckets.drive.IDriveControl;
import org.bitbuckets.drive.controlsds.sds.ISwerveModule;
import org.bitbuckets.lib.log.ILoggable;
import org.littletonrobotics.junction.Logger;

import java.util.List;

/**
 * Represents a real drive controller that implements control of the drivetrain using a list of SwerveModule interfaces
 */
public class DriveControl implements IDriveControl {

    final ILoggable<SwerveModuleState[]> desiredStates;
    final ILoggable<SwerveModuleState[]> actualStates;

    // Swerve Modules
    final ISwerveModule moduleFrontLeft;
    final ISwerveModule moduleFrontRight;
    final ISwerveModule moduleBackLeft;
    final ISwerveModule moduleBackRight;


    //Speed factor that edits the max velocity and max angular velocity
    double speedModifier = .75;

    List<ISwerveModule> modules;

    SwerveModuleState[] cachedSetpoint = new SwerveModuleState[]{
            new SwerveModuleState(),
            new SwerveModuleState(),
            new SwerveModuleState(),
            new SwerveModuleState()
    };

    public DriveControl(ISwerveModule moduleFrontLeft, ISwerveModule moduleFrontRight, ISwerveModule moduleBackLeft, ISwerveModule moduleBackRight, ILoggable<SwerveModuleState[]> desiredStates, ILoggable<SwerveModuleState[]> actualStates) {
        this.moduleFrontLeft = moduleFrontLeft;
        this.moduleFrontRight = moduleFrontRight;
        this.moduleBackLeft = moduleBackLeft;
        this.moduleBackRight = moduleBackRight;
        this.desiredStates = desiredStates;
        this.actualStates = actualStates;

        // if disabled, we don't create a list of modules at all
        if (moduleFrontLeft != null) {
            modules = List.of(
                    moduleFrontLeft,
                    moduleFrontRight,
                    moduleBackLeft,
                    moduleBackRight
            );
        } else {
            modules = List.of();
        }
    }

    public void guaranteedLoggingLoop() {
        Logger.getInstance().recordOutput("a", reportSetpointStates());

        //desiredStates.log(reportSetpointStates());
        //actualStates.log(reportActualStates());
    }

    public SwerveModuleState[] reportSetpointStates() {
        return cachedSetpoint;
    }

    public SwerveModuleState[] reportActualStates() {
        return new SwerveModuleState[]{
                new SwerveModuleState(),
                new SwerveModuleState(),
                new SwerveModuleState(),
                new SwerveModuleState()
        };
    }


    public void drive(ChassisSpeeds chassisSpeeds) {
        doDriveWithStates(DriveConstants.KINEMATICS.toSwerveModuleStates(chassisSpeeds));
    }

    public void stopSticky() {
        doDriveWithStates(new SwerveModuleState[]{
                new SwerveModuleState(0, Rotation2d.fromDegrees(45)), //Front Left
                new SwerveModuleState(0, Rotation2d.fromDegrees(-45)), //Front Right
                new SwerveModuleState(0, Rotation2d.fromDegrees(-45)), //Back Left
                new SwerveModuleState(0, Rotation2d.fromDegrees(45)) //Back Right
        });
    }

    public double getMaxVelocity() {
        return DriveConstants.MAX_DRIVE_VELOCITY * speedModifier;
    }

    public double getMaxAngularVelocity() {
        return DriveConstants.MAX_ANG_VELOCITY * speedModifier;
    }


    public void stop() {
        //tags: low priority
        //TODO this should use x-lock wheel constants
        this.drive(new ChassisSpeeds(0.0, 0.0, 0.0));
    }

    private void doDriveWithStates(SwerveModuleState[] states) {

        if (states != null) {
            cachedSetpoint = states;
            SwerveDriveKinematics.desaturateWheelSpeeds(states, getMaxVelocity());

            for (int i = 0; i < 4; i++) {
                //System.out.println("Module " + i + ": " + states[i].angle.getDegrees());
                modules.get(i).set(velocityToDriveVolts(states[i].speedMetersPerSecond), states[i].angle.getRadians());
            }
        }
    }

    private double velocityToDriveVolts(double speedMetersPerSecond) {
        int maxVoltage = 12;
        double ff = DriveConstants.FF.calculate(speedMetersPerSecond);
        return MathUtil.clamp(ff, -maxVoltage, maxVoltage);
    }

    public SwerveModulePosition[] currentPositions() {
        return new SwerveModulePosition[]{
                new SwerveModulePosition(),
                new SwerveModulePosition(),
                new SwerveModulePosition(),
                new SwerveModulePosition()
        };
    }


}
