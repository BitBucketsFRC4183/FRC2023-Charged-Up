package org.bitbuckets.drive.module;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import org.bitbuckets.drive.fenc.FilteredEncoder;
import org.bitbuckets.lib.hardware.IEncoder;
import org.bitbuckets.lib.hardware.IMotor;
import org.bitbuckets.lib.log.DataLogger;

public class DriveModule {

    final IMotor drive;
    final IMotor turn;
    final IEncoder driveEncoder;
    final FilteredEncoder filteredEncoder;

    final DataLogger<ModuleDataAutoGen> logger;

    DriveModule(IMotor drive, IMotor turn, IEncoder driveEncoder, FilteredEncoder filteredEncoder, DataLogger<ModuleDataAutoGen> logger) {
        this.drive = drive;
        this.turn = turn;
        this.driveEncoder = driveEncoder;
        this.filteredEncoder = filteredEncoder;
        this.logger = logger;
    }

    public SwerveModuleState reportState() {
        double velocity_metersPerSecond = driveEncoder.getVelocityMechanism_metersPerSecond();
        double rotation_radians = filteredEncoder.relative.getMechanismPositionBounded_radians();

        return new SwerveModuleState(velocity_metersPerSecond, Rotation2d.fromRadians(rotation_radians));
    }

    public SwerveModulePosition reportPosition() {
        double position_meters = driveEncoder.getPositionMechanism_meters();
        double rotation_radians = filteredEncoder.relative.getMechanismPositionBounded_radians();

        return new SwerveModulePosition(position_meters, Rotation2d.fromRadians(rotation_radians));
    }

    /**
     * @param velocitySetpoint_metersPerSecond I'm forwarding velocity right now, but turning it into voltage..
     * @param turnSetpoint_radians
     */
    public void commandSetpointValues(double velocitySetpoint_metersPerSecond, double turnSetpoint_radians) {

        double optimizedEncoderRads = filteredEncoder.optimizeSetpointWithMechanismRads_encoderRads(turnSetpoint_radians);
        double optimizeRotations = optimizedEncoderRads / 2.0 / Math.PI;


        drive.moveAtPercent(velocitySetpoint_metersPerSecond); //TODO fix this please
        turn.moveToPosition(optimizeRotations);

        //deal with all this new data!
        logger.process(data -> {
            data.moduleRotSetpoint_rads = turnSetpoint_radians;
            data.optTurnSetpoint_rads = optimizedEncoderRads;
            data.optTurnSetpoint_rot = optimizeRotations;

            data.velSetpoint_ms = velocitySetpoint_metersPerSecond;
        });
    }


}
