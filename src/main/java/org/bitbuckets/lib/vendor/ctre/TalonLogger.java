package org.bitbuckets.lib.vendor.ctre;

import org.bitbuckets.lib.core.HasLogLoop;
import org.bitbuckets.lib.log.ILoggable;

/**
 * Log mattlib related stuff of the SparkRelativeMotorController
 */
public class TalonLogger implements HasLogLoop {

    final TalonRelativeMotorController motorController;

    final ILoggable<Double> positionSetpoint_mechanismRotations;
    final ILoggable<Double> encoderReadout_mechanismRotations;
    final ILoggable<Double> encoder_positionRaw;

    public TalonLogger(TalonRelativeMotorController motorController, ILoggable<Double> positionSetpoint_mechanismRotations, ILoggable<Double> encoderReadout_mechanismRotations, ILoggable<Double> encoderReadoutRawPosition) {
        this.motorController = motorController;
        this.positionSetpoint_mechanismRotations = positionSetpoint_mechanismRotations;
        this.encoderReadout_mechanismRotations = encoderReadout_mechanismRotations;
        this.encoder_positionRaw = encoderReadoutRawPosition;
    }

    @Override
    public void logLoop() {
        positionSetpoint_mechanismRotations.log(motorController.getSetpoint_mechanismRotations());
        encoderReadout_mechanismRotations.log(motorController.getMechanismPositionAccum_rot());
        encoder_positionRaw.log(motorController.getPositionRaw());

    }
}
