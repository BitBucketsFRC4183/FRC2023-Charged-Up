package org.bitbuckets.drive.controlsds;

import com.ctre.phoenix.sensors.WPI_PigeonIMU;
import com.swervedrivespecialties.swervelib.SwerveModule;
import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import org.bitbuckets.drive.DriveSDSConstants;
import org.bitbuckets.lib.log.DataLogger;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a real drive controller that implements control of the drivetrain using a list of SwerveModule interfaces
 */
public class DriveControlSDS {

    final DataLogger<DriveControlSDSDataAutoGen> logger;
    /**
     * The maximum velocity of the robot in meters per second.
     * <p>
     * This is a measure of how fast the robot should be able to drive in a straight
     * line.
     */
    final double maxVelocity_metersPerSecond;
    /**
     * The maximum angular velocity of the robot in radians per second.
     * <p>
     * This is a measure of how fast the robot can rotate in place.
     */
    final double maxAngularVelocity_radiansPerSecond;

    // By default we use a Pigeon for our gyroscope. But if you use another
    // gyroscope, like a NavX, you can change 
    // The important thing about how you configure your gyroscope is that rotating
    // the robot counter-clockwise should
    // cause the angle reading to increase until it wraps back over to zero.
    //  Remove if you are using a Pigeon
    //  Uncomment if you are using a NavX
    final WPI_PigeonIMU gyro;

    // Swerve Modules
    final SwerveModule moduleFrontLeft;
    final SwerveModule moduleFrontRight;
    final SwerveModule moduleBackLeft;
    final SwerveModule moduleBackRight;

    // Instance Variables
    final SwerveDriveKinematics kinematics;

    //Speed factor that edits the max velocity and max angular velocity
    double speedModifier = .75;


    ArrayList<SwerveModule> modules;
    ChassisSpeeds chassisSpeeds;

    SwerveModuleState[] cachedSetpoint = new SwerveModuleState[4];

    public DriveControlSDS(DataLogger<DriveControlSDSDataAutoGen> logger, double maxVelocity_metersPerSecond, double maxAngularVelocity_radiansPerSecond, WPI_PigeonIMU gyro, SwerveModule moduleFrontLeft, SwerveModule moduleFrontRight, SwerveModule moduleBackLeft, SwerveModule moduleBackRight, SwerveDriveKinematics kinematics) {
        this.logger = logger;
        this.maxVelocity_metersPerSecond = maxVelocity_metersPerSecond;
        this.maxAngularVelocity_radiansPerSecond = maxAngularVelocity_radiansPerSecond;
        this.gyro = gyro;
        this.moduleFrontLeft = moduleFrontLeft;
        this.moduleFrontRight = moduleFrontRight;
        this.moduleBackLeft = moduleBackLeft;
        this.moduleBackRight = moduleBackRight;
        this.kinematics = kinematics;

        // We will also create a list of all the modules so we can easily access them later
        modules = new ArrayList<>(List.of(moduleFrontLeft, moduleFrontRight, moduleBackLeft, moduleBackRight));

    }

    void guaranteedLoggingLoop() {
        logger.process(data -> {
            data.targetStates = reportSetpointStates();
            data.realStates = reportActualStates();
        });
    }

    public SwerveModuleState[] reportSetpointStates() {
        return cachedSetpoint;
    }

    public SwerveModuleState[] reportActualStates() {
        if (chassisSpeeds != null) {
            return kinematics.toSwerveModuleStates(chassisSpeeds);
        }
        return new SwerveModuleState[4];
    }

    public SwerveModulePosition[] reportActualPositions() {
        SwerveModulePosition[] positions = new SwerveModulePosition[4];

        // TODO: not implemented
        return positions;
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

    public void zeroGyro() {
        gyro.reset();
    }

    public double getMaxVelocity() {
        return maxVelocity_metersPerSecond * speedModifier;
    }

    public double getMaxAngularVelocity() {
        return maxAngularVelocity_radiansPerSecond * speedModifier;
    }

    public Rotation2d getGyroAngle() {
        return this.gyro.getRotation2d();
    }

    public void stop() {
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