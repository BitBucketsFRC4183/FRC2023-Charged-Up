package org.bitbuckets.lib.vendor.thrifty;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.AnalogInput;
import org.bitbuckets.lib.hardware.IAbsoluteEncoder;
import org.bitbuckets.lib.log.ILoggable;


public class ThriftyEncoder implements IAbsoluteEncoder, Runnable {

    private static final double READ_VOLTAGE_MAX = 4.8;





    final AnalogInput input;
    final ILoggable<double[]> allThriftyEncoderData;




    Rotation2d rotOffset;


    // read voltage, get input
    public ThriftyEncoder(AnalogInput input, ILoggable<double[]> allThriftyEncoderData) {
        this.input = input;
        this.allThriftyEncoderData = allThriftyEncoderData;
        this.rotOffset = new Rotation2d(0.0);
    }

    // read voltage, get input
    public ThriftyEncoder(AnalogInput input, Rotation2d rotOffset, ILoggable<double[]> allThriftyEncoderData) {
        this.input = input;
        this.rotOffset = rotOffset;
        this.allThriftyEncoderData = allThriftyEncoderData;
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
        return (input.getVoltage() * 2 * Math.PI) / READ_VOLTAGE_MAX;
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

    @Override
    public void run() {
        allThriftyEncoderData.log(new double[] {
                getRawPositionRadians(),
                getAbsoluteAngle()
        });
    }
}