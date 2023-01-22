package org.bitbuckets.lib.encoder;

import org.bitbuckets.lib.hardware.IMotorController;

public class TestMotorController implements IMotorController {

    final double mechFactor;
    final double rawToRotFactor;
    final double position;

    public TestMotorController(double mechFactor, double rawToRotFactor, double position) {
        this.mechFactor = mechFactor;
        this.rawToRotFactor = rawToRotFactor;
        this.position = position;
    }

    @Override
    public double getMechanismFactor() {
        return mechFactor;
    }

    @Override
    public double getRotationsToMetersFactor() {
        return 1;
    }

    @Override
    public double getRawToRotationsFactor() {
        return rawToRotFactor;
    }

    @Override
    public double getTimeFactor() {
        return 1;
    }

    @Override
    public double getPositionRaw() {
        return position;
    }

    @Override
    public double getVelocityRaw() {
        return 1;
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
