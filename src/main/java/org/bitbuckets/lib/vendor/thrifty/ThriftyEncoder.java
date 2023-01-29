package org.bitbuckets.lib.vendor.thrifty;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import org.bitbuckets.lib.hardware.IAbsoluteEncoder;
import org.bitbuckets.lib.log.Loggable;

public class ThriftyEncoder implements IAbsoluteEncoder {
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
        SmartDashboard.putNumber("Position", getRawPositionRadians());
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
        double absAngle = (input.getVoltage() / 5) * 2 * Math.PI;
        SmartDashboard.putNumber("Absolute Angle Radians", absAngle);
        return absAngle;
    }
}