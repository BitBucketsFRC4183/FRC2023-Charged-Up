package org.bitbuckets.drive.controlsds;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import org.bitbuckets.drive.DriveConstants;
import org.bitbuckets.drive.IDriveControl;
import org.bitbuckets.drive.controlsds.sds.SwerveModule;
import org.bitbuckets.lib.log.ILoggable;

import java.util.ArrayList;

/**
 * Represents a real drive controller that implements control of the drivetrain using a list of SwerveModule interfaces
 */
public class DriveControlSDS implements IDriveControl {

    final ILoggable<SwerveModuleState[]> desiredStates;
    final ILoggable<SwerveModuleState[]> actualStates;

    // Swerve Modules
    final SwerveModule moduleFrontLeft;
    final SwerveModule moduleFrontRight;
    final SwerveModule moduleBackLeft;
    final SwerveModule moduleBackRight;


    //Speed factor that edits the max velocity and max angular velocity
    double speedModifier = .75;


    ArrayList<SwerveModule> modules;
    ChassisSpeeds chassisSpeeds;

    SwerveModuleState[] cachedSetpoint = new SwerveModuleState[4];

    public DriveControlSDS(ILoggable<SwerveModuleState[]> desiredStates, ILoggable<SwerveModuleState[]> actualStates, SwerveModule moduleFrontLeft, SwerveModule moduleFrontRight, SwerveModule moduleBackLeft, SwerveModule moduleBackRight) {
        this.desiredStates = desiredStates;
        this.actualStates = actualStates;
        this.moduleFrontLeft = moduleFrontLeft;
        this.moduleFrontRight = moduleFrontRight;
        this.moduleBackLeft = moduleBackLeft;
        this.moduleBackRight = moduleBackRight;
    }


    public void guaranteedLoggingLoop() {
        desiredStates.log(reportSetpointStates());
        actualStates.log(reportActualStates());
    }

    public SwerveModuleState[] reportSetpointStates() {
        return new SwerveModuleState[]{
                new SwerveModuleState(),
                new SwerveModuleState(),
                new SwerveModuleState(),
                new SwerveModuleState()
        };
    }

    public SwerveModuleState[] reportActualStates() {
        return new SwerveModuleState[]{
                new SwerveModuleState(),
                new SwerveModuleState(),
                new SwerveModuleState(),
                new SwerveModuleState()
        };
    }


    public SwerveModulePosition[] reportActualPositions() {
        return new SwerveModulePosition[] {
                new SwerveModulePosition(),
                new SwerveModulePosition(),
                new SwerveModulePosition(),
                new SwerveModulePosition()
        };
    }

    public void drive(ChassisSpeeds chassisSpeeds) {
        this.chassisSpeeds = chassisSpeeds;

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
        cachedSetpoint = states;
    }

    private double velocityToDriveVolts(double speedMetersPerSecond) {
        int maxVoltage = 12;
        double ff = DriveConstants.FF.calculate(speedMetersPerSecond);
        return MathUtil.clamp(ff, -maxVoltage, maxVoltage);
    }


    @Override
    public SwerveModulePosition[] currentPositions() {
        return reportActualPositions();
    }
}