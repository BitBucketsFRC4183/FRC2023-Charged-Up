package org.bitbuckets.lib.vendor.matt;

import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.ProcessPath;
import org.bitbuckets.lib.hardware.IMotorController;

/**
 * Does nothing
 */
public class NoTalonSetup implements ISetup<IMotorController>, IMotorController {


    @Override
    public IMotorController build(ProcessPath path) {
        return this;
    }

    @Override
    public double getMechanismFactor() {
        return 0;
    }

    @Override
    public double getRotationsToMetersFactor() {
        return 0;
    }

    @Override
    public double getRawToRotationsFactor() {
        return 0;
    }

    @Override
    public double getTimeFactor() {
        return 0;
    }

    @Override
    public double getPositionRaw() {
        return 0;
    }

    @Override
    public double getVelocityRaw() {
        return 0;
    }

    @Override
    public void forceOffset(double offsetUnits_baseUnits) {

    }

    @Override
    public void moveAtPercent(double percent) {

    }

    @Override
    public void moveToPosition(double position_encoderRotations) {

    }

    @Override
    public void moveAtVelocity(double velocity_encoderMetersPerSecond) {

    }

    @Override
    public double getSetpoint_rawUnits() {
        return 0;
    }

    @Override
    public <T> T rawAccess(Class<T> clazz) throws UnsupportedOperationException {
        return null;
    }
}
