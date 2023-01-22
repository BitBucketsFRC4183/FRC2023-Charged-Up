package org.bitbuckets.drive.module;

import com.ctre.phoenix.sensors.WPI_CANCoder;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import org.bitbuckets.drive.control.CTREModuleState;
import org.bitbuckets.drive.fenc.OptimizeEncoderWrapper;
import org.bitbuckets.lib.hardware.IEncoder;
import org.bitbuckets.lib.hardware.IMotor;
import org.bitbuckets.lib.log.DataLogger;
import org.bitbuckets.lib.util.AngleUtil;

public class DriveModule {

    final IMotor drive;
    final IMotor turn;
    final IEncoder driveEncoder;
    final IEncoder turnEncoder;

    final DataLogger<DriveModuleDataAutoGen> logger;

    public DriveModule(IMotor drive, IMotor turn, IEncoder driveEncoder, IEncoder turnEncoder, DataLogger<DriveModuleDataAutoGen> logger) {
        this.drive = drive;
        this.turn = turn;
        this.driveEncoder = driveEncoder;
        this.turnEncoder = turnEncoder;
        this.logger = logger;
    }

    public SwerveModuleState reportState() {
        double velocity_metersPerSecond = driveEncoder.getVelocityMechanism_metersPerSecond();
        double rotation_radians = turnEncoder.getMechanismPositionBounded_radians();

        return new SwerveModuleState(velocity_metersPerSecond, Rotation2d.fromRadians(rotation_radians));
    }

    public SwerveModulePosition reportPosition() {
        double position_meters = driveEncoder.getPositionMechanism_meters();
        double rotation_radians = turnEncoder.getMechanismPositionBounded_radians();

        return new SwerveModulePosition(position_meters, Rotation2d.fromRadians(rotation_radians));
    }

    /**
     *
     */
    public void commandSetpointValues(SwerveModuleState state) {

        //SwerveModuleState realState = CTREModuleState.optimize(state, Rotation2d.fromRadians(turnEncoder.getMechanismPositionBounded_radians()));

        double encoderRads = new OptimizeEncoderWrapper(turnEncoder).optimizeSetpoint_encoderRads(state.angle.getRadians());
        double encoderRotations = encoderRads / (Math.PI / 2.0);

        drive.moveAtPercent(state.speedMetersPerSecond); //TODO fix this please
        turn.moveToPosition(encoderRotations);

        //deal with all this new data!
        logger.process(data -> {
            data.lastOptTurnSetpoint_rads = encoderRads;
            data.lastOptTurnSetpoint_rot = encoderRotations;
        });
    }

    /**
     * This method should force the hall effect encoders to be at whatever the absolute encoders say they are
     * Useful for making sure you aren't going insane
     */
    public void resetToAbsoluteEncoderRecommendation() {

    }


}
