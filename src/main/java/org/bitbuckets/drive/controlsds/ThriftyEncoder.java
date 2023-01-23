package org.bitbuckets.drive.controlsds;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.AnalogInput;

public class ThriftyEncoder implements AbsoluteEncoder {
    public AnalogInput input;
    public Rotation2d rotOffset;
    public double readVoltageMax;

    private static double standardReadVoltageMax = 4.8;

    // read voltage, get input
    public ThriftyEncoder(AnalogInput input, double readVoltageMax) {
        this.input = input;
        this.rotOffset = new Rotation2d(0.0);
        this.readVoltageMax = readVoltageMax;
    }

    // read voltage, get input
    public ThriftyEncoder(AnalogInput input, Rotation2d rotOffset, double readVoltageMax) {
        this.input = input;
        this.rotOffset = rotOffset;
        this.readVoltageMax = readVoltageMax;
    }

    // read voltage, get input
    public ThriftyEncoder(AnalogInput input) {
        this.input = input;
        this.rotOffset = new Rotation2d(0.0);
        this.readVoltageMax = ThriftyEncoder.standardReadVoltageMax;
    }

    // read voltage, get input
    public ThriftyEncoder(AnalogInput input, Rotation2d rotOffset) {
        this.input = input;
        this.rotOffset = rotOffset;
        this.readVoltageMax = ThriftyEncoder.standardReadVoltageMax;
    }

    // get position
    public Rotation2d getRawPosition() {
        return new Rotation2d(this.getRawPositionRadians());
    }

    public double getPositionRadians() {
        return this.getRawPositionRadians() + this.rotOffset.getRadians();
    }

    public Rotation2d getPosition() {
        return this.getRawPosition().plus(this.rotOffset);
    }

    public double getRawPositionRadians() {
        return (input.getVoltage() * 2 * Math.PI) / this.readVoltageMax;
    }

    public Rotation2d get() {
        return this.getPosition();
    }

    public void resetPosition() {
        rotOffset = getRawPosition();
    }

    @Override
    public double getAbsoluteAngle() {
        return (input.getVoltage() / 5) * 2 * Math.PI;
    }
}