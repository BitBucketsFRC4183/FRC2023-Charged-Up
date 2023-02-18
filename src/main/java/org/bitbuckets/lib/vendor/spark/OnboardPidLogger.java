package org.bitbuckets.lib.vendor.spark;

import org.bitbuckets.lib.util.HasLogLoop;
import org.bitbuckets.lib.log.ILoggable;

/**
 * Log mattlib related stuff of the SparkRelativeMotorController
 */
public class OnboardPidLogger implements HasLogLoop {

    final SparkRelativeMotorController motorController;

    final ILoggable<Double> positionSetpoint_mechanismRotations;
    final ILoggable<Double> encoderReadout_mechanismRotations;
    final ILoggable<Double> error_mechanismRotations;
    final ILoggable<LastControlMode> lastControlMode;

    public OnboardPidLogger(SparkRelativeMotorController motorController, ILoggable<Double> positionSetpoint_mechanismRotations, ILoggable<Double> encoderReadout_mechanismRotations, ILoggable<Double> error_mechanismRotations, ILoggable<LastControlMode> lastControlMode) {
        this.motorController = motorController;
        this.positionSetpoint_mechanismRotations = positionSetpoint_mechanismRotations;
        this.encoderReadout_mechanismRotations = encoderReadout_mechanismRotations;
        this.error_mechanismRotations = error_mechanismRotations;
        this.lastControlMode = lastControlMode;
    }

    @Override
    public void logLoop() {
        positionSetpoint_mechanismRotations.log( motorController.getSetpoint_mechanismRotations() );
        encoderReadout_mechanismRotations.log( motorController.getMechanismPositionAccum_rot() );
        error_mechanismRotations.log( motorController.getError_mechanismRotations());

        lastControlMode.log( motorController.lastControlMode );
    }
}
