package org.bitbuckets.drive.controlsds;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import org.bitbuckets.drive.DriveConstants;
import org.bitbuckets.drive.DriveSDSConstants;
import org.bitbuckets.drive.controlsds.sds.ISwerveModule;
import org.bitbuckets.lib.log.DataLogger;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a real drive controller that implements control of the drivetrain using a list of SwerveModule interfaces
 */
public class DriveControlSDS {

    final DataLogger<DriveControlSDSDataAutoGen> logger;

    // Swerve Modules
    final ISwerveModule moduleFrontLeft;
    final ISwerveModule moduleFrontRight;
    final ISwerveModule moduleBackLeft;
    final ISwerveModule moduleBackRight;

    // Instance Variables
    final SwerveDriveKinematics kinematics;

    //Speed factor that edits the max velocity and max angular velocity
    double speedModifier = .75;


    ArrayList<ISwerveModule> modules;
    ChassisSpeeds chassisSpeeds;

    SwerveModuleState[] cachedSetpoint = new SwerveModuleState[4];


    public DriveControlSDS(DataLogger<DriveControlSDSDataAutoGen> logger, ISwerveModule moduleFrontLeft, ISwerveModule moduleFrontRight, ISwerveModule moduleBackLeft, ISwerveModule moduleBackRight, SwerveDriveKinematics kinematics) {
        this.logger = logger;
        this.moduleFrontLeft = moduleFrontLeft;
        this.moduleFrontRight = moduleFrontRight;
        this.moduleBackLeft = moduleBackLeft;
        this.moduleBackRight = moduleBackRight;
        this.kinematics = kinematics;

        // We will also create a list of all the modules so we can easily access them later
        modules = new ArrayList<>(List.of(moduleFrontLeft, moduleFrontRight, moduleBackLeft, moduleBackRight));

    }

    public void guaranteedLoggingLoop() {
        logger.process(data -> {
            data.targetStates = reportSetpointStates();
            data.realStates = reportActualStates();
        });
    }


    //TODO fix this shit
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


    public void drive(ChassisSpeeds chassisSpeeds) {
        this.chassisSpeeds = chassisSpeeds;

        doDriveWithStates(this.kinematics.toSwerveModuleStates(chassisSpeeds));
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
        double ff = DriveSDSConstants.feedForward.calculate(speedMetersPerSecond);
        return MathUtil.clamp(ff, -maxVoltage, maxVoltage);
    }


}