package org.bitbuckets.drive.controlsds;

import config.Drive;
import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import org.bitbuckets.drive.IDriveControl;
import org.bitbuckets.drive.controlsds.sds.ISwerveModule;
import org.bitbuckets.lib.core.HasLogLoop;
import org.bitbuckets.lib.log.IDebuggable;

import java.util.List;

/**
 * Represents a real drive controller that implements control of the drivetrain using a list of SwerveModule interfaces
 */
public class DriveControl implements IDriveControl, HasLogLoop {

    final SwerveDriveKinematics kinematics;
    final IDebuggable debug;

    // Swerve Modules
    final ISwerveModule moduleFrontLeft;
    final ISwerveModule moduleFrontRight;
    final ISwerveModule moduleBackLeft;
    final ISwerveModule moduleBackRight;


    //Speed factor that edits the max velocity and max angular velocity
    double speedModifier = 0.75;

    List<ISwerveModule> modules;

    SwerveModuleState[] cachedSetpoint = new SwerveModuleState[]{
            new SwerveModuleState(),
            new SwerveModuleState(),
            new SwerveModuleState(),
            new SwerveModuleState()
    };

    public DriveControl(SwerveDriveKinematics kinematics, IDebuggable debug, ISwerveModule moduleFrontLeft, ISwerveModule moduleFrontRight, ISwerveModule moduleBackLeft, ISwerveModule moduleBackRight) {
        this.kinematics = kinematics;
        this.moduleFrontLeft = moduleFrontLeft;
        this.moduleFrontRight = moduleFrontRight;
        this.moduleBackLeft = moduleBackLeft;
        this.moduleBackRight = moduleBackRight;
        this.debug = debug;
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

    @Override
    public void drive(ChassisSpeeds chassisSpeeds) {
        doDriveWithStates(kinematics.toSwerveModuleStates(chassisSpeeds));
    }


    @Override
    public void stop() {
        doDriveWithStates(new SwerveModuleState[]{
                new SwerveModuleState(0, Rotation2d.fromDegrees(0)),
                new SwerveModuleState(0, Rotation2d.fromDegrees(0)),
                new SwerveModuleState(0, Rotation2d.fromDegrees(0)),
                new SwerveModuleState(0, Rotation2d.fromDegrees(0))
        });
    }
    @Override
    public void stopSticky() {
        doDriveWithStates(new SwerveModuleState[]{
                new SwerveModuleState(0, Rotation2d.fromDegrees(45)),
                new SwerveModuleState(0, Rotation2d.fromDegrees(-45)),
                new SwerveModuleState(0, Rotation2d.fromDegrees(-45)),
                new SwerveModuleState(0, Rotation2d.fromDegrees(45))
        });
    }


    public double getMaxVelocity() {
        return Drive.MAX_DRIVE_VELOCITY * speedModifier;
    }

    public double getMaxAngularVelocity() {
        return Drive.MAX_ANG_VELOCITY * speedModifier;
    }


    private void doDriveWithStates(SwerveModuleState[] states) {

        if (states != null) {
            cachedSetpoint = states;
            SwerveDriveKinematics.desaturateWheelSpeeds(states, getMaxVelocity());

            for (int i = 0; i < 4; i++) {
                //System.out.println("Module " + i + ": " + states[i].angle.getDegrees());
                int maxVoltage = 12;
                double ff = Drive.FF.calculate(states[i].speedMetersPerSecond);
                modules.get(i).set(MathUtil.clamp(ff, -maxVoltage, maxVoltage), states[i].angle.getRadians());
            }
        }
    }

    //microoptimization: do this without stream()
    public SwerveModulePosition[] currentPositions() {

        debug.log("Pos 0 Dis", this.modules.get(3).getPosition().distanceMeters);
        debug.log("Pos 0 Angle", this.modules.get(3).getPosition().angle.getDegrees());
        return new SwerveModulePosition[]
                {
                        this.modules.get(0).getPosition(),
                        this.modules.get(1).getPosition(),
                        this.modules.get(2).getPosition(),
                        this.modules.get(3).getPosition()
                };

    }


    SwerveModuleState[] currentStates() {
        return this.modules.stream().map(ISwerveModule::getState).toArray(SwerveModuleState[]::new);
    }

    @Override
    public void logLoop() {
        debug.log("command-modules", cachedSetpoint);
        debug.log("actual-modules", currentStates());
        debug.log("actual-positions", currentPositions());
    }
}
